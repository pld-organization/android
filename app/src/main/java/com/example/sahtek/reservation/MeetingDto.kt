package com.example.sahtek.reservation

data class MeetingDto(
    val reservationId: String = "",
    val doctorId: String? = null,
    val patientId: String? = null,
    val reason: String = "",
    val meetingUrl: String = "",
    val meetingRoomName: String = "",
    val day: String = "",
    val startTime: String = "",
    val endTime: String = ""
)
