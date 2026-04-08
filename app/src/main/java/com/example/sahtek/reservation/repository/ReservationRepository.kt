package com.example.sahtek.reservation.repository

import com.example.sahtek.reservation.CreateReservationRequestDto
import com.example.sahtek.reservation.ReservationResult

interface ReservationRepository {
    suspend fun createReservation(
        token: String,
        request: CreateReservationRequestDto
    ): ReservationResult
}
