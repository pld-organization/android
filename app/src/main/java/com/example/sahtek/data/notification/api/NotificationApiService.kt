package com.example.sahtek.data.notification.api

import com.example.sahtek.data.notification.model.NotificationDto
import com.example.sahtek.data.notification.model.ReservationCreatedNotificationRequestDto
import com.example.sahtek.data.notification.model.ReservationCancelledNotificationRequestDto
import com.example.sahtek.data.notification.model.MeetingReminderNotificationRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApiService {
    
    // GET /notifications/me - Get all notifications
    @GET("notifications/me")
    suspend fun getMyNotifications(
        @Header("Authorization") token: String
    ): Response<List<NotificationDto>>
    
    // PATCH /notifications/{id}/read - Mark one notification as read
    @PATCH("notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Header("Authorization") token: String,
        @Path("id") notificationId: String
    ): Response<Void>
    
    // PATCH /notifications/me/read-all - Mark all as read
    @PATCH("notifications/me/read-all")
    suspend fun markAllNotificationsAsRead(
        @Header("Authorization") token: String
    ): Response<Void>
    
    // DELETE /notifications/{id} - Delete one notification
    @DELETE("notifications/{id}")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("id") notificationId: String
    ): Response<Void>
    
    // POST /notifications/reservation-created - Notify reservation created
    @POST("notifications/reservation-created")
    suspend fun notifyReservationCreated(
        @Header("Authorization") token: String,
        @Body request: ReservationCreatedNotificationRequestDto
    ): Response<Void>
    
    // POST /notifications/reservation-cancelled - Notify reservation cancelled
    @POST("notifications/reservation-cancelled")
    suspend fun notifyReservationCancelled(
        @Header("Authorization") token: String,
        @Body request: ReservationCancelledNotificationRequestDto
    ): Response<Void>
    
    // POST /notifications/meeting-reminder - Notify meeting reminder
    @POST("notifications/meeting-reminder")
    suspend fun notifyMeetingReminder(
        @Header("Authorization") token: String,
        @Body request: MeetingReminderNotificationRequestDto
    ): Response<Void>
}
