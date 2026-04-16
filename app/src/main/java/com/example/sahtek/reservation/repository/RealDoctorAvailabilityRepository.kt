package com.example.sahtek.reservation.repository

import com.example.sahtek.reservation.CreateScheduleRequestDto
import com.example.sahtek.reservation.DoctorAvailabilityResult
import com.example.sahtek.reservation.ReservationApiService

class RealDoctorAvailabilityRepository(
    private val api: ReservationApiService
) : DoctorAvailabilityRepository {

    override suspend fun createDoctorSchedule(token: String, request: CreateScheduleRequestDto): DoctorAvailabilityResult {
        return try {
            val response = api.createDoctorSchedule(token, request)

            if (response.isSuccessful) {
                val message = response.body()
                    ?.message
                    ?.takeIf { it.isNotBlank() }
                    ?: "Availability saved successfully."

                DoctorAvailabilityResult.Success(message)
            } else {
                val errorBody = response.errorBody()
                    ?.string()
                    ?.takeIf { it.isNotBlank() }

                DoctorAvailabilityResult.Error(
                    errorBody ?: "Failed to save availability (${response.code()})."
                )
            }
        } catch (e: Exception) {
            DoctorAvailabilityResult.Error(e.message ?: "Failed to save availability.")
        }
    }
}
