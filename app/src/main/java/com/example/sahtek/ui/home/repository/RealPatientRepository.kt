package com.example.sahtek.ui.home.repository

import android.util.Log
import com.authservice.SessionManager
import com.example.sahtek.data.doctor.model.DoctorApiService
import com.example.sahtek.data.doctor.model.DoctorDto
import com.example.sahtek.data.doctor.model.DoctorResponseParser
import com.example.sahtek.exception.TokenExpiredException
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.reservation.ReservationApiService
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.ui.home.viewmodel.AppointmentUi
import com.example.sahtek.ui.home.viewmodel.HomeQuickStats
import com.example.sahtek.ui.home.viewmodel.PationHome
import com.example.sahtek.ui.home.viewmodel.PatientApiService
import com.example.sahtek.ui.profile.ProfileResult
import com.example.sahtek.ui.profile.UpdateProfileRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
            coroutineScope {
                // Fetch profile first (required for patient ID)
                val profileDef = async { apiService.getPatientProfile("Bearer $token") }
                val profileResponse = profileDef.await()

                if (!profileResponse.isSuccessful) {
                    if (profileResponse.code() == 401) {
                        sessionManager.clearSession()
                        throw TokenExpiredException()
                    }
                    return@coroutineScope PationHome(
                        isLoading = false, 
                        errorMessage = "Error ${profileResponse.code()}: ${profileResponse.errorBody()?.string() ?: "Unknown error"}"
                    )
                }

                val body = profileResponse.body() ?: return@coroutineScope PationHome(isLoading = false, errorMessage = "Empty response")

                val fullName = listOf(body.firstName, body.lastName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .ifBlank { "Patient" }

                // Fetch doctor directory and reservations in parallel
                val doctorDirectoryDef = async { fetchDoctorDirectory(token) }
                val reservationsDef = async { 
                    try {
                        reservationApiService.getPatientReservations("Bearer $token", body.id).body() ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("RealPatientRepository", "Failed to fetch reservations", e)
                        emptyList()
                    }
                }

                val doctorDirectory = doctorDirectoryDef.await()
                val reservations = reservationsDef.await()

                val appointments = mutableListOf<AppointmentUi>()
                var nextAppt: AppointmentUi? = null
                var upcomingCount = 0

                reservations.forEach { res ->
                    appointments.add(res.toAppointmentUi(doctorDirectory[res.doctorId]))
                }

                upcomingCount = reservations.count { it.reservationStatus == "PENDING" }
                
                val firstPending = reservations.firstOrNull { it.reservationStatus == "PENDING" }
                if (firstPending != null) {
                    nextAppt = firstPending.toAppointmentUi(doctorDirectory[firstPending.doctorId])
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
                        reportsCount = 0,
                        historyCount = appointments.size
                    )
                )
            }
        } catch (e: TokenExpiredException) {
            throw e
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
                // Check if token expired (401 Unauthorized)
                if (response.code() == 401) {
                    sessionManager.clearSession()
                    throw TokenExpiredException()
                }
                val errorJson = response.errorBody()?.string() ?: "Unknown error"
                Log.e("API_ERROR", "Code: ${response.code()}, Body: $errorJson")
                ProfileResult.Error(errorJson)
            }
        } catch (e: TokenExpiredException) {
            throw e
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", e.message ?: "No message", e)
            ProfileResult.Error("Connection failed: ${e.localizedMessage}")
        }
    }

    private suspend fun fetchDoctorDirectory(token: String): Map<String, DoctorDto> {
        return try {
            val response = doctorApiService.getAllDoctors("Bearer $token")
            if (!response.isSuccessful) {
                if (response.code() == 401) {
                    sessionManager.clearSession()
                    throw TokenExpiredException()
                }
                return emptyMap()
            }

            val doctors = DoctorResponseParser.parseDoctors(response.body())
            
            // Identify doctors that need detail fetching
            val doctorsNeedingDetails = doctors.filter { doctor ->
                doctor.id.isNotBlank() && !doctor.hasHumanName
            }

            // If no doctors need details, return early
            if (doctorsNeedingDetails.isEmpty()) {
                return doctors
                    .filter { it.id.isNotBlank() }
                    .associateBy { it.id }
            }

            // Fetch all doctor details in parallel
            coroutineScope {
                val detailDefs = doctorsNeedingDetails.map { doctor ->
                    async { doctor.id to fetchDoctorDetails(token, doctor.id) }
                }
                
                val detailedDoctors = detailDefs.map { it.await() }.toMap()
                
                val enrichedDoctors = doctors.map { doctor ->
                    if (doctor.id.isBlank() || doctor.hasHumanName) {
                        doctor
                    } else {
                        detailedDoctors[doctor.id] ?: doctor
                    }
                }
                
                enrichedDoctors
                    .filter { it.id.isNotBlank() }
                    .associateBy { it.id }
            }
        } catch (e: TokenExpiredException) {
            throw e
        } catch (e: Exception) {
            Log.e("RealPatientRepository", "Failed to fetch doctors", e)
            emptyMap()
        }
    }

    private suspend fun fetchDoctorDetails(token: String, doctorId: String): DoctorDto? {
        return try {
            val response = doctorApiService.getDoctorById("Bearer $token", doctorId)
            if (!response.isSuccessful) {
                if (response.code() == 401) {
                    sessionManager.clearSession()
                    throw TokenExpiredException()
                }
                return null
            }

            DoctorResponseParser.parseDoctor(response.body(), fallbackId = doctorId)
        } catch (e: TokenExpiredException) {
            throw e
        } catch (e: Exception) {
            Log.e("RealPatientRepository", "Failed to fetch doctor details for $doctorId", e)
            null
        }
    }
}

private fun ReservationResponseDto.toAppointmentUi(doctor: DoctorDto?): AppointmentUi =
    AppointmentUi(
        id = id ?: "",
        doctortName = doctor?.fullName ?: "Doctor ${doctorId?.takeLast(4) ?: "Unknown"}",
        doctorSpeciality = doctor?.speciality
            ?.trim()
            ?.takeIf { it.isNotBlank() && !it.equals("nothing", ignoreCase = true) }
            ?: "Specialist",
        date = reservationDay ?: "N/A",
        time = reservationTime ?: "N/A",
        status = reservationStatus,
        doctorId = doctorId ?: "",
        patientId = patientId ?: ""
    )
