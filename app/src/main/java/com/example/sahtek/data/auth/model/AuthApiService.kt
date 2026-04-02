package com.example.sahtek.data.auth.model

import com.example.sahtek.data.auth.model.AuthResponse
import com.example.sahtek.data.auth.model.LoginRequest
import com.example.sahtek.data.auth.model.PatientSignupRequest
import com.example.sahtek.data.auth.model.DoctorSignupRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun patientSignup(
        @Body request: PatientSignupRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun doctorSignup(
        @Body request: DoctorSignupRequest
    ): Response<AuthResponse>

    @POST("/auth/google/mobile")
    suspend fun googleMobileAuth(
        @Body request: GoogleMobileAuthRequest
    ): Response<AuthResponse>

}

