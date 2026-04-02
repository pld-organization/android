package com.example.sahtek.ui.profile

/**
 * Flat request model matching the structure shown in Postman.
 */
data class UpdateProfileRequest(
    val email: String? = null,
    val password: String? = null,
    val role: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val speciality: String? = null,
    val establishment: String? = null
)
