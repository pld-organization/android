package com.example.sahtek.reservation

data class AvailableTimeSlotDto(
    val id: String? = null,
    val doctorId: String? = null,
    val dayOfWeek: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val appointmentType: String? = null,
    val isAvailable: Boolean = false
)
