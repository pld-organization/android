package com.example.sahtek.reservation

import com.example.sahtek.ui.home.AvailableScheduleUi

data class ReservationUiState(
    val doctorId: String = "",
    val patientId: String = "",
    val reservationDay: String = "",
    val reservationTime: String = "",
    val reason: String = "",
    val reservationStatus: Boolean = true,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val availableSlots: List<AvailableScheduleUi> = emptyList()
)