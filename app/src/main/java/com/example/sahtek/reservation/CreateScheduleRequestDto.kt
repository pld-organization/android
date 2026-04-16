package com.example.sahtek.reservation

import com.google.gson.annotations.SerializedName

data class CreateScheduleRequestDto(
    val doctorId: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    @SerializedName("appointmentType")
    val appointmentType: String
)
