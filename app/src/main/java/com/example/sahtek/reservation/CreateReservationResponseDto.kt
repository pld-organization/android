package com.example.sahtek.reservation

data class CreateReservationResponseDto(
    val message: String? = null,
    val success: Boolean? = null,
    val data: ReservationData? = null
)

data class ReservationData(
    val reservationId: String? = null,
)
