package com.example.sahtek.data.auth.model

data class PatientSignupRequest(
    val email: String,
    val password: String,
    val role: String = "PATIENT",
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val bloodType: String,
    val gender: String
)


