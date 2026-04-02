package com.example.sahtek.data.auth.repository

import com.authservice.SessionManager
import com.example.sahtek.data.auth.model.AuthApiService
import com.example.sahtek.data.auth.model.AuthResponse
import com.example.sahtek.data.auth.model.AuthResult
import com.example.sahtek.data.auth.model.DoctorSignupRequest
import com.example.sahtek.data.auth.model.GoogleMobileAuthRequest
import com.example.sahtek.data.auth.model.LoginRequest
import com.example.sahtek.data.auth.model.PatientSignupRequest
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.ui.home.viewmodel.PatientApiService

class FakeAuthRepository(
    private val apiService: AuthApiService = RetrofitClient.authApiService,
    private val profileApiService: PatientApiService = RetrofitClient.patientApiService,
    private val sessionManager: SessionManager? = null
) : AuthRepository {

    override suspend fun login(request: LoginRequest): AuthResult {
        return try {
            val response = apiService.login(
                LoginRequest(
                    email = request.email,
                    password = request.password
                )
            )

            if (response.isSuccessful) {
                val body = response.body()

                createSuccessResult(body)

            } else {
                AuthResult.Error("Login failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun signupPatient(request: PatientSignupRequest): AuthResult {
        return try {
            val response = apiService.patientSignup(request)

            if (response.isSuccessful) {
                val body = response.body()

                createSuccessResult(body)
            } else {
                AuthResult.Error(response.body()?.message ?: "Patient signup failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun signupDoctor(request: DoctorSignupRequest): AuthResult {
        return try {
            val response = apiService.doctorSignup(request)

            if (response.isSuccessful) {
                val body = response.body()

                createSuccessResult(body)
            } else {
                AuthResult.Error(response.body()?.message ?: "Doctor signup failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun googleMobileAuth(request: GoogleMobileAuthRequest): AuthResult {
        return try {
            val response = apiService.googleMobileAuth(request)

            if (response.isSuccessful) {
                val body = response.body()

                createSuccessResult(body)
            } else {
                AuthResult.Error("Google login failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error")
        }
    }

    private suspend fun createSuccessResult(body: AuthResponse?): AuthResult.Success {
        val token = body?.token ?: body?.accessToken ?: ""
        val normalizedRole = body?.role
            ?.trim()
            ?.uppercase()
            ?.takeIf { it.isNotBlank() }
            ?: resolveRoleFromProfile(token)

        token.takeIf { it.isNotBlank() }?.let { sessionManager?.saveAuthToken(it)
        }
        normalizedRole?.let { sessionManager?.saveUserRole(it) }

        return AuthResult.Success(
            token = token,
            role = normalizedRole
        )
    }

    private suspend fun resolveRoleFromProfile(token: String): String? {
        if (token.isBlank()) return null

        return try {
            val response = profileApiService.getPatientProfile("Bearer $token")
            if (!response.isSuccessful) return null

            response.body()
                ?.role
                ?.trim()
                ?.uppercase()
                ?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
