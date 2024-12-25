package com.example.timely1.Notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.timely1.R

@RequiresApi(Build.VERSION_CODES.O)
class NotificationHelper(private val context: Context) {

    private val channelId = "reminder_channel_4"
    private val channelName = "Reminders"
    private val notificationId = 2

    init {
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for reminder notifications"
            enableVibration(true) // Включаем вибрацию
            vibrationPattern = longArrayOf(0, 400, 200, 400, 200 , 400) // Новый паттерн вибрации
        }

        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(name: String, time: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent для открытия главного Activity
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Создание уведомления
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Нагадування про запис")
            .setContentText("$name записан(а) на $time")
            .setSmallIcon(R.drawable.check) // Убедитесь, что у вас есть иконка с этим ID
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Высокий приоритет
            .setContentIntent(pendingIntent) // Добавляем PendingIntent
            .setAutoCancel(true) // Убираем уведомление после нажатия
            .build()

        // Показ уведомления
        notificationManager.notify(notificationId, notification)
    }
}
