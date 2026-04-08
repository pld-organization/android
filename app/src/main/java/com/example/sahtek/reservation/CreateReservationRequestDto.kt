package com.example.sahtek.reservation

data class CreateReservationRequestDto(
    val doctorId: String,
    val patientId: String,
    val reservationDate: String,
    val reservationTime: String,
    val reason: String,
    val reservationStatus: String
)