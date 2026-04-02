package com.example.sahtek.ui.auth




data class SignupUiState(
    val firstName: String = "",
    val lastName: String = "",
    val speciality: String = "",
    val establishment: String = "",
    val bloodType: String = "",
    val selectedGender: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignupSuccess: Boolean = false
)
