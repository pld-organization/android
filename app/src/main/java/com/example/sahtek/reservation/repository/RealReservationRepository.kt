package com.example.sahtek.reservation.repository

import com.example.sahtek.reservation.ReservationApiService
import com.example.sahtek.reservation.CreateReservationRequestDto
import com.example.sahtek.reservation.ReservationResult

class RealReservationRepository(
    private val api: ReservationApiService
) : ReservationRepository {

    override suspend fun createReservation(
        token: String,
        request: CreateReservationRequestDto
    ): ReservationResult {
        return try {
            val response = api.createReservation("Bearer $token", request)
            ReservationResult.Success(response)
        } catch (e: Exception) {
            ReservationResult.Error(e.message ?: "Failed to create reservation")
        }
    }
}
