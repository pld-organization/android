package com.example.sahtek.data.notification.repository

import com.authservice.SessionManager
import com.example.sahtek.data.notification.api.NotificationApiService
import com.example.sahtek.data.notification.model.NotificationDto
import com.example.sahtek.data.notification.model.ReservationCreatedNotificationRequestDto
import com.example.sahtek.data.notification.model.ReservationCancelledNotificationRequestDto
import com.example.sahtek.data.notification.model.MeetingReminderNotificationRequestDto

class NotificationRepositoryImpl(
    private val apiService: NotificationApiService,
    private val sessionManager: SessionManager
) : NotificationRepository {
    
    override suspend fun getMyNotifications(): Result<List<NotificationDto>> {
        return try {
            val token = sessionManager.getAuthToken() ?: return Result.failure(Exception("No token available"))
            val response = apiService.getMyNotifications("Bearer $token")
            
            if (response.isSuccessful) {
                val notifications = response.body() ?: emptyList()
                Result.success(notifications)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        return try {
            val token = sessionManager.getAuthToken() ?: return Result.failure(Exception("No token available"))
            val response = apiService.markNotificationAsRead("Bearer $token", notificationId)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markAllNotificationsAsRead(): Result<Unit> {
        return try {
            val token = sessionManager.getAuthToken() ?: return Result.failure(Exception("No token available"))
            val response = apiService.markAllNotificationsAsRead("Bearer $token")
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            val token = sessionManager.getAuthToken() ?: return Result.failure(Exception("No token available"))
            val response = apiService.deleteNotification("Bearer $token", notificationId)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun notifyReservationCreated(request: ReservationCreatedNotificationRequestDto): Result<Unit> {
        return try {
            val token = sessionManager.getAuthToken() ?: return Result.failure(Exception("No token available"))
            val response = apiService.notifyReservationCreated("Bearer $token", request)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun notifyReservationCancelled(request: ReservationCancelledNotificationRequestDto): Result<Unit> {
        return try {
            val token = sessionManager.getAuthToken() ?: return Result.failure(Exception("No token available"))
            val response = apiService.notifyReservationCancelled("Bearer $token", request)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun notifyMeetingReminder(request: MeetingReminderNotificationRequestDto): Result<Unit> {
        return try {
            val token = sessionManager.getAuthToken() ?: return Result.failure(Exception("No token available"))
            val response = apiService.notifyMeetingReminder("Bearer $token", request)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
