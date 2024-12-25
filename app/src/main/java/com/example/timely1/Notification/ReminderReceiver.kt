package com.example.timely1.Notification


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

class ReminderReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra("client_name") ?: "Клиент"
        val dateTime = intent.getStringExtra("client_time") ?: "время неизвестно"

        // Проверяем состояние переключателя
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false)

        if (!notificationsEnabled) {
            // Если уведомления отключены, ничего не показываем
            return
        }

        // Извлекаем только время из строки с датой и временем
        val time = extractTime(dateTime)

        val notificationHelper = NotificationHelper(context)
        notificationHelper.sendNotification(name, time)
    }

    // Метод для извлечения только времени (часы и минуты) из строки
    private fun extractTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val date = inputFormat.parse(dateTime)
            outputFormat.format(date)
        } catch (e: Exception) {
            "время неизвестно"
        }
    }
}


