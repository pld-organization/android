package com.example.sahtek.data.auth.model

data class DoctorSignupRequest(
    val email: String,
    val password: String,
    val role: String = "DOCTOR",
    val firstName: String,
    val lastName: String,
    val speciality: String,
    val establishment: String
)


