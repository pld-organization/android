package com.example.sahtek.ui.consultation

import com.example.sahtek.reservation.MeetingDto

data class ConsultationUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val meetings: List<MeetingDto> = emptyList(),
    val selectedMeetingUrl: String? = null,
    val successMessage: String? = null
)
