package com.example.sahtek.ui.notification

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object NotificationTimeFormatter {
    
    fun formatCreatedAt(isoDateString: String): String {
        return try {
            val instant = Instant.parse(isoDateString)
            val now = Instant.now()
            val minutesDiff = ChronoUnit.MINUTES.between(instant, now)
            val hoursDiff = ChronoUnit.HOURS.between(instant, now)
            val daysDiff = ChronoUnit.DAYS.between(instant, now)
            
            when {
                minutesDiff < 1 -> "Just now"
                minutesDiff < 60 -> "$minutesDiff min ago"
                hoursDiff < 24 -> "$hoursDiff hour${if (hoursDiff > 1) "s" else ""} ago"
                daysDiff < 7 -> "$daysDiff day${if (daysDiff > 1) "s" else ""} ago"
                else -> {
                    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                    localDateTime.format(formatter)
                }
            }
        } catch (e: Exception) {
            isoDateString
        }
    }
}
