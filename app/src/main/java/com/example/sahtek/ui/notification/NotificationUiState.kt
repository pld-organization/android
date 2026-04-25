package com.example.sahtek.ui.notification

import com.example.sahtek.data.notification.model.NotificationDto

data class NotificationUiState(
    val notifications: List<NotificationDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val unreadCount: Int = 0,
    val isRefreshing: Boolean = false,
    val successMessage: String? = null
) {
    fun updateUnreadCount(): NotificationUiState {
        val count = notifications.count { !it.isRead }
        return this.copy(unreadCount = count)
    }
}
