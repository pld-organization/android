package com.example.sahtek.ui.notification

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.data.notification.repository.NotificationRepositoryImpl
import com.example.sahtek.ui.notification.components.EmptyNotificationState
import com.example.sahtek.ui.notification.components.ErrorNotificationState
import com.example.sahtek.ui.notification.components.LoadingNotificationState
import com.example.sahtek.ui.notification.components.NotificationCard

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Create the repository and ViewModel
    val repository = remember(context) {
        NotificationRepositoryImpl(
            apiService = RetrofitClient.notificationApiService,
            sessionManager = SessionManager(context)
        )
    }
    
    val viewModel: NotificationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationViewModel(repository) as T
            }
        }
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF3B7FE5)
                    )
                }
                
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
            }
            
            // Mark all as read button
            if (uiState.unreadCount > 0) {
                Row(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF3B7FE5).copy(alpha = 0.08f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.markAllAsRead() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Mark all as read",
                        tint = Color(0xFF3B7FE5),
                        modifier = Modifier.padding(0.dp)
                    )
                    Text(
                        text = "Tout marquer comme lu",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3B7FE5)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFE5E7EB)))
        
        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingNotificationState()
                }
                uiState.errorMessage != null -> {
                    ErrorNotificationState(
                        errorMessage = uiState.errorMessage ?: "Unknown error",
                        onRetry = { viewModel.loadNotifications() }
                    )
                }
                uiState.notifications.isEmpty() -> {
                    EmptyNotificationState()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.notifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onMarkAsRead = {
                                    if (!notification.isRead) {
                                        viewModel.markNotificationAsRead(notification.id)
                                    }
                                },
                                onDelete = {
                                    viewModel.deleteNotification(notification.id)
                                },
                                modifier = Modifier
                                    .clickable {
                                        if (!notification.isRead) {
                                            viewModel.markNotificationAsRead(notification.id)
                                        }
                                    }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}
