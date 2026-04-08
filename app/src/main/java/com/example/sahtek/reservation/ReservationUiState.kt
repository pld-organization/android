package com.example.sahtek.reservation


data class ReservationUiState(
    val doctorId: String = "",
    val patientId: String = "",
    val reservationDay: String = "MONDAY",
    val reservationTime: String = "",
    val reason: String = "",
    val reservationStatus: Boolean = true,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)