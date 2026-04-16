package com.example.sahtek.reservation

data class ReservationResponseDto(
    val id: String,
    val doctorId: String,
    val patientId: String,
    val reservationDay: String,
    val reservationTime: String,
    val reason: String,
    val reservationStatus: String,
    val createdAt: String,
    val updatedAt: String
)
