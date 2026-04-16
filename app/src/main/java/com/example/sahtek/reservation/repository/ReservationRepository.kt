package com.example.sahtek.reservation.repository

import com.example.sahtek.reservation.CreateReservationRequestDto
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.reservation.ReservationResult
import com.example.sahtek.reservation.AvailableTimeSlotDto

interface ReservationRepository {
    suspend fun createReservation(
        token: String,
        request: CreateReservationRequestDto
    ): ReservationResult

    suspend fun getDoctorReservations(
        token: String,
        doctorId: String
    ): List<ReservationResponseDto>

    suspend fun getPatientReservations(
        token: String,
        patientId: String
    ): List<ReservationResponseDto>

    suspend fun getAvailableTimeSlots(
        token: String,
        doctorId: String
    ): List<AvailableTimeSlotDto>

    suspend fun cancelReservation(
        token: String,
        reservationId: String
    ): ReservationResult
}
