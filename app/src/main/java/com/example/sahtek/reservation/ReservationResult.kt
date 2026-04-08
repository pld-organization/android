package com.example.sahtek.reservation


import com.example.sahtek.reservation.CreateReservationResponseDto

sealed class ReservationResult {
    data class Success(val data: CreateReservationResponseDto) : ReservationResult()
    data class Error(val message: String) : ReservationResult()
}