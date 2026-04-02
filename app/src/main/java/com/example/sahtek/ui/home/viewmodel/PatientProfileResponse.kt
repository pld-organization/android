package com.example.sahtek.ui.home.viewmodel

data class PatientProfileResponse(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val createdAt: String,
    val patient: PatientDetailsResponse?,
    val doctor: DoctorDetailsResponse?
)

data class PatientDetailsResponse(
    val dateOfBirth: String?,
    val phoneNumber: String?,
    val bloodType: String?,
    val gender: String?
)

data class DoctorDetailsResponse(
    val speciality: String?,
    val establishment: String?
)

data class UpdateProfileResponse(
    val message: String?,
    val profile: PatientProfileResponse?
)
