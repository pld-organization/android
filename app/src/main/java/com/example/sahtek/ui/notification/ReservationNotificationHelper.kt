package com.example.sahtek.ui.notification

import com.example.sahtek.data.notification.repository.NotificationRepository
import com.example.sahtek.data.notification.model.ReservationCreatedNotificationRequestDto
import com.example.sahtek.data.notification.model.ReservationCancelledNotificationRequestDto

/**
 * Helper class to trigger notification events from reservation flow.
 * 
 * Usage in your reservation creation or cancellation code:
 * 
 * // After successful reservation creation
 * ReservationNotificationHelper.notifyReservationCreated(
 *     repository = notificationRepository,
 *     reservationId = "reservation-id",
 *     doctorId = "doctor-id",
 *     patientId = "patient-id",
 *     reservationDay = "MONDAY",
 *     reservationTime = "09:00",
 *     meetingUrl = "https://youraccount.daily.co/room-abc123",
 *     reason = "Annual checkup"
 * )
 * 
 * // After successful reservation cancellation
 * ReservationNotificationHelper.notifyReservationCancelled(
 *     repository = notificationRepository,
 *     reservationId = "reservation-id",
 *     doctorId = "doctor-id",
 *     patientId = "patient-id",
 *     reservationDay = "MONDAY",
 *     reservationTime = "09:00"
 * )
 */
object ReservationNotificationHelper {
    
    suspend fun notifyReservationCreated(
        repository: NotificationRepository,
        reservationId: String,
        doctorId: String,
        patientId: String,
        reservationDay: String,
        reservationTime: String,
        meetingUrl: String,
        reason: String
    ) {
        val request = ReservationCreatedNotificationRequestDto(
            reservationId = reservationId,
            doctorId = doctorId,
            patientId = patientId,
            reservationDay = reservationDay,
            reservationTime = reservationTime,
            meetingUrl = meetingUrl,
            reason = reason
        )
        
        repository.notifyReservationCreated(request)
            .onSuccess {
                // Notification event sent successfully
            }
            .onFailure { error ->
                // Handle error - notification failed but don't fail the reservation
                // Log or display message to user
            }
    }
    
    suspend fun notifyReservationCancelled(
        repository: NotificationRepository,
        reservationId: String,
        doctorId: String,
        patientId: String,
        reservationDay: String,
        reservationTime: String
    ) {
        val request = ReservationCancelledNotificationRequestDto(
            reservationId = reservationId,
            doctorId = doctorId,
            patientId = patientId,
            reservationDay = reservationDay,
            reservationTime = reservationTime
        )
        
        repository.notifyReservationCancelled(request)
            .onSuccess {
                // Notification event sent successfully
            }
            .onFailure { error ->
                // Handle error - notification failed but don't fail the cancellation
                // Log or display message to user
            }
    }
}
