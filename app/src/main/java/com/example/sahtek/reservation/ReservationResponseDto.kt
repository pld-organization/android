package com.example.sahtek.reservation

import com.google.gson.annotations.SerializedName

data class ReservationResponseDto(
    val id: String? = null,
    val doctorId: String? = null,
    val patientId: String? = null,
    val reservationDay: String? = null,
    val reservationTime: String? = null,
    val reason: String? = null,
    @SerializedName("reservationStatus")
    val rawStatus: Any? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val reservationStatus: String
        get() = when (rawStatus) {
            is Boolean -> if (rawStatus) "CONFIRMED" else "CANCELLED"
            is String -> rawStatus
            else -> "PENDING"
        }
}
