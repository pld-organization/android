package com.example.sahtek.reservation.repository

import com.example.sahtek.reservation.ReservationApiService
import com.example.sahtek.reservation.CreateReservationRequestDto
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.reservation.ReservationResult
import com.example.sahtek.reservation.AvailableTimeSlotDto
import com.example.sahtek.reservation.CreateReservationResponseDto

class RealReservationRepository(
    private val api: ReservationApiService
) : ReservationRepository {

    override suspend fun createReservation(
        token: String,
        request: CreateReservationRequestDto
    ): ReservationResult {
        return try {
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.createReservation(authToken, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ReservationResult.Success(body)
                } else {
                    ReservationResult.Error("Empty response from server")
                }
            } else {
                ReservationResult.Error("Failed to create reservation: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ReservationResult.Error(e.message ?: "Failed to create reservation")
        }
    }

    override suspend fun getDoctorReservations(
        token: String,
        doctorId: String
    ): List<ReservationResponseDto> {
        return try {
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.getDoctorReservations(authToken, doctorId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPatientReservations(
        token: String,
        patientId: String
    ): List<ReservationResponseDto> {
        return try {
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.getPatientReservations(authToken, patientId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAvailableTimeSlots(
        token: String,
        doctorId: String
    ): List<AvailableTimeSlotDto> {
        return try {
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.getAvailableTimeSlots(authToken, doctorId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun cancelReservation(
        token: String,
        reservationId: String
    ): ReservationResult {
        return try {
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.cancelReservation(authToken, reservationId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ReservationResult.Success(
                        CreateReservationResponseDto(
                            message = body.message,
                            success = body.success
                        )
                    )
                } else {
                    ReservationResult.Error("Empty response from server")
                }
            } else {
                ReservationResult.Error("Failed to cancel reservation: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ReservationResult.Error(e.message ?: "Failed to cancel reservation")
        }
    }
}
