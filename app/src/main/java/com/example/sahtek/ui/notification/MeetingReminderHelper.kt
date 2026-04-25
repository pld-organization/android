package com.example.sahtek.ui.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object MeetingReminderHelper {

    /**
     * Schedules a local notification 15 minutes before the meeting start time.
     * 
     * @param context Application context
     * @param reservationId Unique identifier for the reservation to avoid duplicate alarms
     * @param doctorName Name of the doctor to show in the notification
     * @param meetingUrl The online meeting URL (if any)
     * @param dateString The date of the reservation (e.g., "2023-10-15")
     * @param timeString The start time of the reservation (e.g., "14:00")
     */
    fun scheduleReminder(
        context: Context,
        reservationId: String,
        doctorName: String,
        meetingUrl: String,
        dateString: String,
        timeString: String
    ) {
        try {
            // Attempt to parse the date and time
            // We assume the format might be "yyyy-MM-dd" and "HH:mm" or "HH:mm:ss"
            // If the date is just a day of week like "MONDAY", this simple logic won't work perfectly 
            // without knowing the exact upcoming date. Assuming standard format for now.
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dateWithTime = "$dateString $timeString"
            val meetingDate = format.parse(dateWithTime) ?: return

            val calendar = Calendar.getInstance()
            calendar.time = meetingDate
            // Subtract 15 minutes
            calendar.add(Calendar.MINUTE, -15)

            val alarmTimeMillis = calendar.timeInMillis
            val currentTimeMillis = System.currentTimeMillis()

            // Do not schedule if the reminder time is already in the past
            if (alarmTimeMillis <= currentTimeMillis) {
                Log.d("MeetingReminder", "Alarm time is in the past, skipping: $reservationId")
                return
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            // Check for exact alarm permission on Android 12+ (S)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.w("MeetingReminder", "Cannot schedule exact alarms - missing SCHEDULE_EXACT_ALARM permission")
                return
            }

            val intent = Intent(context, MeetingReminderReceiver::class.java).apply {
                putExtra("reservationId", reservationId)
                putExtra("doctorName", doctorName)
                putExtra("meetingUrl", meetingUrl)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reservationId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTimeMillis,
                pendingIntent
            )
            
            Log.d("MeetingReminder", "Scheduled reminder for $reservationId at ${calendar.time}")
            
        } catch (e: Exception) {
            Log.e("MeetingReminder", "Failed to schedule reminder for $reservationId: ${e.message}", e)
        }
    }
    
    fun cancelReminder(context: Context, reservationId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MeetingReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reservationId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("MeetingReminder", "Cancelled reminder for $reservationId")
        }
    }
}
