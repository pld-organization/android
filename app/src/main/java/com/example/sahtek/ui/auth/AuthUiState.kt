package com.example.sahtek.ui.auth

data class AuthUiState (


    val email: String = "",
    val password: String = "",
    val currentUserRole: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? =null,
    val isLoginSuccess: Boolean = false,
    val successMessage: String? = null,
)


