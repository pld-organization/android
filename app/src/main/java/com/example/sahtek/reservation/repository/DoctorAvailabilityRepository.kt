package com.example.sahtek.reservation.repository

import com.example.sahtek.reservation.CreateScheduleRequestDto
import com.example.sahtek.reservation.DoctorAvailabilityResult

interface DoctorAvailabilityRepository {
    suspend fun createDoctorSchedule(token: String, request: CreateScheduleRequestDto): DoctorAvailabilityResult
}
