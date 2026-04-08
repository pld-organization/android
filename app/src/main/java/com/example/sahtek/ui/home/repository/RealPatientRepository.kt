package com.example.sahtek.ui.home.repository

import com.authservice.SessionManager
import com.example.sahtek.ui.home.viewmodel.PationHome
import com.example.sahtek.ui.home.viewmodel.PatientApiService
import com.example.sahtek.ui.profile.ProfileResult
import com.example.sahtek.ui.profile.UpdateProfileRequest
import android.util.Log

class RealPatientRepository(
    private val apiService: PatientApiService,
    private val sessionManager: SessionManager
) : PatientRepository {

    override suspend fun getPatientHome(): PationHome {
        val token = sessionManager.getAuthToken()
            ?: return PationHome(isLoading = false, errorMessage = "No authentication token found")

        return try {
            val response = apiService.getPatientProfile("Bearer $token")

            if (!response.isSuccessful) {
                return PationHome(
                    isLoading = false, 
                    errorMessage = "Error ${response.code()}: ${response.errorBody()?.string() ?: "Unknown error"}"
                )
            }

            val body = response.body() ?: return PationHome(isLoading = false, errorMessage = "Empty response")

            val fullName = listOf(body.firstName, body.lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Patient" }

            PationHome(
                id = body.id, // Populating the ID here
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
                nextAppointment = null,
                latestAiResult = null,
                quickStats = com.example.sahtek.ui.home.viewmodel.HomeQuickStats()
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
}
