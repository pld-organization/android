package com.example.sahtek.data.notification.model

import com.google.gson.annotations.SerializedName

data class ReservationCreatedNotificationRequestDto(
    @SerializedName("reservationId")
    val reservationId: String,
    
    @SerializedName("doctorId")
    val doctorId: String,
    
    @SerializedName("patientId")
    val patientId: String,
    
    @SerializedName("reservationDay")
    val reservationDay: String,
    
    @SerializedName("reservationTime")
    val reservationTime: String,
    
    @SerializedName("meetingUrl")
    val meetingUrl: String,
    
    @SerializedName("reason")
    val reason: String
)

data class ReservationCancelledNotificationRequestDto(
    @SerializedName("reservationId")
    val reservationId: String,
    
    @SerializedName("doctorId")
    val doctorId: String,
    
    @SerializedName("patientId")
    val patientId: String,
    
    @SerializedName("reservationDay")
    val reservationDay: String,
    
    @SerializedName("reservationTime")
    val reservationTime: String
)

data class MeetingReminderNotificationRequestDto(
    @SerializedName("reservationId")
    val reservationId: String,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("meetingUrl")
    val meetingUrl: String,
    
    @SerializedName("minutesBefore")
    val minutesBefore: Int
)
