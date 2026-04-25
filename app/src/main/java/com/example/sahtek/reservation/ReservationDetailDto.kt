package com.example.sahtek.reservation

/**
 * Detailed reservation response that includes schedule info and meeting data.
 * Used for endpoints like GET /reservation/{reservationId} and listing reservations.
 */
data class ReservationDetailDto(
    val id: String = "",
    val patientId: String = "",
    val doctorId: String = "",
    val reason: String = "",
    val reservationStatus: Boolean = false,
    val meetingUrl: String? = null,
    val meetingRoomName: String? = null,
    val schedule: ScheduleDto? = null
)
