package com.example.sahtek.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sahtek.data.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = repository.getMyNotifications()
            result.onSuccess { notifications ->
                _uiState.update { state ->
                    state.copy(
                        notifications = notifications.sortedByDescending { it.createdAt },
                        isLoading = false,
                        errorMessage = null
                    ).updateUnreadCount()
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load notifications"
                    )
                }
            }
        }
    }
    
    fun refreshNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            val result = repository.getMyNotifications()
            result.onSuccess { notifications ->
                _uiState.update { state ->
                    state.copy(
                        notifications = notifications.sortedByDescending { it.createdAt },
                        isRefreshing = false,
                        errorMessage = null
                    ).updateUnreadCount()
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        errorMessage = error.message ?: "Failed to refresh notifications"
                    )
                }
            }
        }
    }
    
    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            val result = repository.markNotificationAsRead(notificationId)
            result.onSuccess {
                _uiState.update { state ->
                    val updatedNotifications = state.notifications.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }
                    state.copy(notifications = updatedNotifications).updateUnreadCount()
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Failed to mark notification as read")
                }
            }
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            val result = repository.markAllNotificationsAsRead()
            result.onSuccess {
                _uiState.update { state ->
                    val updatedNotifications = state.notifications.map { it.copy(isRead = true) }
                    state.copy(
                        notifications = updatedNotifications,
                        successMessage = "All notifications marked as read"
                    ).updateUnreadCount()
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Failed to mark all as read")
                }
            }
        }
    }
    
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val result = repository.deleteNotification(notificationId)
            result.onSuccess {
                _uiState.update { state ->
                    val updatedNotifications = state.notifications.filter { it.id != notificationId }
                    state.copy(
                        notifications = updatedNotifications,
                        successMessage = "Notification deleted"
                    ).updateUnreadCount()
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Failed to delete notification")
                }
            }
        }
    }
    
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
