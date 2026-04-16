package com.example.sahtek.ui.home.repository

import android.util.Log
import com.authservice.SessionManager
import com.example.sahtek.data.doctor.model.DoctorApiService
import com.example.sahtek.data.doctor.model.DoctorDto
import com.example.sahtek.data.doctor.model.DoctorResponseParser
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.reservation.ReservationApiService
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.ui.home.viewmodel.AppointmentUi
import com.example.sahtek.ui.home.viewmodel.HomeQuickStats
import com.example.sahtek.ui.home.viewmodel.PationHome
import com.example.sahtek.ui.home.viewmodel.PatientApiService
import com.example.sahtek.ui.profile.ProfileResult
import com.example.sahtek.ui.profile.UpdateProfileRequest

class RealPatientRepository(
    private val apiService: PatientApiService,
    private val reservationApiService: ReservationApiService,
    private val sessionManager: SessionManager,
    private val doctorApiService: DoctorApiService = RetrofitClient.doctorApiService
) : PatientRepository {

    override suspend fun getPatientHome(): PationHome {
        val token = sessionManager.getAuthToken()
            ?: return PationHome(isLoading = false, errorMessage = "No authentication token found")

        return try {
            val profileResponse = apiService.getPatientProfile("Bearer $token")

            if (!profileResponse.isSuccessful) {
                return PationHome(
                    isLoading = false, 
                    errorMessage = "Error ${profileResponse.code()}: ${profileResponse.errorBody()?.string() ?: "Unknown error"}"
                )
            }

            val body = profileResponse.body() ?: return PationHome(isLoading = false, errorMessage = "Empty response")

            val fullName = listOf(body.firstName, body.lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Patient" }

            // Fetch reservations to populate counts and next appointment
            var nextAppt: AppointmentUi? = null
            val appointments = mutableListOf<AppointmentUi>()
            var upcomingCount = 0
            val doctorDirectory = fetchDoctorDirectory(token)
            
            try {
                val resResponse = reservationApiService.getPatientReservations("Bearer $token", body.id)
                if (resResponse.isSuccessful) {
                    val reservations = resResponse.body() ?: emptyList()
                    
                    reservations.forEach { res ->
                        appointments.add(res.toAppointmentUi(doctorDirectory[res.doctorId]))
                    }

                    upcomingCount = reservations.count { it.reservationStatus == "PENDING" }
                    
                    val firstPending = reservations.firstOrNull { it.reservationStatus == "PENDING" }
                    if (firstPending != null) {
                        nextAppt = firstPending.toAppointmentUi(doctorDirectory[firstPending.doctorId])
                    }
                }
            } catch (e: Exception) {
                Log.e("RealPatientRepository", "Failed to fetch reservations", e)
            }

            PationHome(
                id = body.id,
                isLoading = false,
                patientname = fullName,
                email = body.email,
                role = body.role,
                phoneNumber = body.patient?.phoneNumber.orEmpty(),
                dateOfBirth = body.patient?.dateOfBirth.orEmpty(),
                gender = body.patient?.gender.orEmpty(),
                speciality = body.doctor?.speciality.orEmpty(),
                establishment = body.doctor?.establishment.orEmpty(),
                unreadnotification = 0,
                nextAppointment = nextAppt,
                appointments = appointments,
                latestAiResult = null,
                quickStats = HomeQuickStats(
                    upcomingCount = upcomingCount,
                    reportsCount = 0, // No API for this yet
                    historyCount = appointments.size
                )
            )
        } catch (e: Exception) {
            PationHome(isLoading = false, errorMessage = "Network Error: ${e.message}")
        }
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): ProfileResult {
        val token = sessionManager.getAuthToken()
            ?: return ProfileResult.Error("No token found")

        return try {
            Log.d("API_UPDATE", "Sending request: $request")

            val response = apiService.updatePatientProfile("Bearer $token", request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ProfileResult.Success(body)
                } else {
                    ProfileResult.Error("Update successful but response body was empty.")
                }
            } else {
                val errorJson = response.errorBody()?.string() ?: "Unknown error"
                Log.e("API_ERROR", "Code: ${response.code()}, Body: $errorJson")
                ProfileResult.Error(errorJson)
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", e.message ?: "No message", e)
            ProfileResult.Error("Connection failed: ${e.localizedMessage}")
        }
    }

    private suspend fun fetchDoctorDirectory(token: String): Map<String, DoctorDto> {
        return try {
            val response = doctorApiService.getAllDoctors("Bearer $token")
            if (response.isSuccessful) {
                val doctors = DoctorResponseParser.parseDoctors(response.body())
                    .map { doctor ->
                        if (doctor.id.isBlank() || doctor.hasHumanName) {
                            doctor
                        } else {
                            fetchDoctorDetails(token, doctor.id) ?: doctor
                        }
                    }
                doctors
                    .filter { it.id.isNotBlank() }
                    .associateBy { it.id }
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            Log.e("RealPatientRepository", "Failed to fetch doctors", e)
            emptyMap()
        }
    }

    private suspend fun fetchDoctorDetails(token: String, doctorId: String): DoctorDto? {
        return try {
            val response = doctorApiService.getDoctorById("Bearer $token", doctorId)
            if (!response.isSuccessful) return null

            DoctorResponseParser.parseDoctor(response.body(), fallbackId = doctorId)
        } catch (e: Exception) {
            Log.e("RealPatientRepository", "Failed to fetch doctor details for $doctorId", e)
            null
        }
    }
}

private fun ReservationResponseDto.toAppointmentUi(doctor: DoctorDto?): AppointmentUi =
    AppointmentUi(
        id = id,
        doctortName = doctor?.fullName ?: "Doctor ${doctorId.takeLast(4)}",
        doctorSpeciality = doctor?.speciality
            ?.trim()
            ?.takeIf { it.isNotBlank() && !it.equals("nothing", ignoreCase = true) }
            ?: "Specialist",
        date = reservationDay,
        time = reservationTime,
        status = reservationStatus
    )
