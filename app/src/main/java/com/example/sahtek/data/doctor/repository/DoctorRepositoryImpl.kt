package com.example.sahtek.data.doctor.repository

import com.authservice.SessionManager
import com.example.sahtek.data.doctor.model.DoctorApiService
import com.example.sahtek.data.doctor.model.DoctorDto
import com.example.sahtek.data.doctor.model.DoctorResponseParser
import com.example.sahtek.exception.TokenExpiredException
import com.example.sahtek.network.RetrofitClient

class DoctorRepositoryImpl(
    private val sessionManager: SessionManager,
    private val apiService: DoctorApiService = RetrofitClient.doctorApiService
) : DoctorRepository {

    override suspend fun getDoctors(): Result<List<DoctorDto>> {
        return try {
            val token = sessionManager.getAuthToken()
                ?: return Result.failure(IllegalStateException("No authentication token found"))

            val response = apiService.getAllDoctors("Bearer $token")
            if (response.isSuccessful) {
                val doctors = DoctorResponseParser.parseDoctors(response.body())
                Result.success(resolveDoctorNames(token, doctors))
            } else {
                // Check if token expired (401 Unauthorized)
                if (response.code() == 401) {
                    sessionManager.clearSession()
                    Result.failure(TokenExpiredException())
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            }
        } catch (e: TokenExpiredException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun resolveDoctorNames(token: String, doctors: List<DoctorDto>): List<DoctorDto> {
        return doctors.map { doctor ->
            if (doctor.id.isBlank() || doctor.hasHumanName) {
                doctor
            } else {
                fetchDoctorDetails(token, doctor.id) ?: doctor
            }
        }
    }

    private suspend fun fetchDoctorDetails(token: String, doctorId: String): DoctorDto? {
        return try {
            val response = apiService.getDoctorById("Bearer $token", doctorId)
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
        } catch (_: Exception) {
            null
        }
    }
}
