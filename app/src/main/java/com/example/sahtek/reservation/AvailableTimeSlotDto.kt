package com.example.sahtek.reservation

data class AvailableTimeSlotDto(
    val id: String,
    val doctorId: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val appointmentType: String,
    val isAvailable: Boolean
)
