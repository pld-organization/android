package com.example.sahtek.data.auth.repository

import com.example.sahtek.data.auth.model.LoginRequest
import com.example.sahtek.data.auth.model.PatientSignupRequest
import com.example.sahtek.data.auth.model.AuthResult
import com.example.sahtek.data.auth.model.DoctorSignupRequest
import com.example.sahtek.data.auth.model.GoogleMobileAuthRequest


interface AuthRepository{
    suspend fun login(request: LoginRequest): AuthResult
    suspend fun signupPatient(request: PatientSignupRequest): AuthResult
    suspend fun signupDoctor(request: DoctorSignupRequest): AuthResult

    suspend fun googleMobileAuth(request: GoogleMobileAuthRequest): AuthResult

}
