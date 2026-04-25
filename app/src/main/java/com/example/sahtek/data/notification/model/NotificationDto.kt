package com.example.sahtek.data.notification.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class NotificationDto(
    @SerializedName("id")
    val id: String = UUID.randomUUID().toString(),
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("type")
    val type: String, // RESERVATION_CREATED, RESERVATION_CANCELLED, MEETING_REMINDER
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("payload")
    val payload: NotificationPayloadDto? = null,
    
    @SerializedName("isRead")
    val isRead: Boolean = false,
    
    @SerializedName("createdAt")
    val createdAt: String // ISO 8601 format: "2026-04-20T21:47:14.565Z"
)

data class NotificationPayloadDto(
    @SerializedName("reservationId")
    val reservationId: String? = null,
    
    @SerializedName("patientId")
    val patientId: String? = null,
    
    @SerializedName("doctorId")
    val doctorId: String? = null,
    
    @SerializedName("reason")
    val reason: String? = null,
    
    @SerializedName("meetingUrl")
    val meetingUrl: String? = null,
    
    @SerializedName("reservationDay")
    val reservationDay: String? = null,
    
    @SerializedName("reservationTime")
    val reservationTime: String? = null
)
