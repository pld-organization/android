package com.example.sahtek.reservation

data class ScheduleDto(
    val id: String = "",
    val doctorId: String = "",
    val dayOfWeek: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val appointmenttype: String = "",
    val status: Boolean = false
)
