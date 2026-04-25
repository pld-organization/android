package com.example.sahtek.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sahtek.data.notification.repository.NotificationRepository

class NotificationViewModelFactory(
    private val repository: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationViewModel(repository) as T
    }
}
