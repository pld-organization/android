package com.example.sahtek.reservation

data class DoctorAvailabilityUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
