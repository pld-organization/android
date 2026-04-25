package com.example.sahtek.ui.notification.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sahtek.data.notification.model.NotificationDto
import com.example.sahtek.ui.notification.NotificationTimeFormatter

@Composable
fun NotificationCard(
    notification: NotificationDto,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val backgroundColor = if (notification.isRead) {
        Color(0xFFFAFCFE)
    } else {
        Color(0xFFEFF5FE)
    }
    
    val borderColor = if (notification.isRead) {
        Color(0xFFE5E7EB)
    } else {
        Color(0xFF3B7FE5).copy(alpha = 0.2f)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(11.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Title
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Message
                    Text(
                        text = notification.message,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF6B7280),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Time
                    Text(
                        text = NotificationTimeFormatter.formatCreatedAt(notification.createdAt),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF9CA3AF)
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete notification",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }

            // "Rejoindre la consultation" button when meetingUrl is present
            val meetingUrl = notification.payload?.meetingUrl
            if (!meetingUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B7FE5),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rejoindre la consultation",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // Mark as read indicator
            if (!notification.isRead) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF3B7FE5).copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Tap to mark as read",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3B7FE5)
                    )
                }
            }
        }
    }
}
