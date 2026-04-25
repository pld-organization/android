package com.example.sahtek.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sahtek.MainActivity
import com.example.sahtek.R

class MeetingReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val meetingUrl = intent.getStringExtra("meetingUrl") ?: ""
        val doctorName = intent.getStringExtra("doctorName") ?: "votre médecin"
        val reservationId = intent.getStringExtra("reservationId") ?: ""

        showNotification(context, doctorName, meetingUrl, reservationId)
    }

    private fun showNotification(context: Context, doctorName: String, meetingUrl: String, reservationId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "meeting_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Rappels de consultation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications de rappel pour vos consultations en ligne"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Open app when notification clicked, or directly open meeting URL if provided
        val pendingIntent: PendingIntent
        if (meetingUrl.isNotBlank()) {
            val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            pendingIntent = PendingIntent.getActivity(
                context,
                reservationId.hashCode(),
                urlIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            pendingIntent = PendingIntent.getActivity(
                context,
                reservationId.hashCode(),
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Use app icon as fallback
            .setContentTitle("Consultation imminente")
            .setContentText("Votre consultation en ligne avec $doctorName commence dans 15 minutes. Préparez-vous à rejoindre la réunion.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(reservationId.hashCode(), notification)
    }
}
