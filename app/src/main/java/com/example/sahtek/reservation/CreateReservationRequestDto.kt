package com.example.sahtek.reservation

data class CreateReservationRequestDto(
    val doctorId: String,
    val patientId: String,
    val reservationDay: String,
    val reservationTime: String,
    val reason: String
)
