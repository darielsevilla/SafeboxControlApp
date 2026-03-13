package com.example.securityboxcontrol.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.securityboxcontrol.MainActivity
import com.example.securityboxcontrol.R

private const val CHANNEL_ID = "esp32_alert_channel"
private const val CHANNEL_NAME = "ESP32 Alerts"
private const val CHANNEL_DESCRIPTION = "Alerts from the smart security box"
private const val NOTIFICATION_ID = 2001

fun Context.showEsp32AlertNotification(message: String) {
    try {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("Notification", "Llego a build check")
        // Create notification channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("Notification", "Paso a build check")
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            notificationManager.createNotificationChannel(channel)
        }

        Log.d("Notification", "Llego a intent")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("Notification", "Llego a notificación")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // puedes cambiarlo luego por un icono de notificación
            .setContentTitle("Caja fuerte")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }catch(e : SecurityException){
        Log.d("Notification", "${e.printStackTrace()}")
    }
}