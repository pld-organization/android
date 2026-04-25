package com.example.sahtek.data.notification.repository

import com.example.sahtek.data.notification.model.NotificationDto
import com.example.sahtek.data.notification.model.ReservationCreatedNotificationRequestDto
import com.example.sahtek.data.notification.model.ReservationCancelledNotificationRequestDto
import com.example.sahtek.data.notification.model.MeetingReminderNotificationRequestDto

interface NotificationRepository {
    
    suspend fun getMyNotifications(): Result<List<NotificationDto>>
    
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>
    
    suspend fun markAllNotificationsAsRead(): Result<Unit>
    
    suspend fun deleteNotification(notificationId: String): Result<Unit>
    
    suspend fun notifyReservationCreated(request: ReservationCreatedNotificationRequestDto): Result<Unit>
    
    suspend fun notifyReservationCancelled(request: ReservationCancelledNotificationRequestDto): Result<Unit>
    
    suspend fun notifyMeetingReminder(request: MeetingReminderNotificationRequestDto): Result<Unit>
}
