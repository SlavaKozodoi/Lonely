package com.example.timely1

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.timely1.DataBase.DataBase
import com.example.timely1.Notification.ReminderReceiver
import java.text.SimpleDateFormat
import java.util.Locale

class Setings : Fragment() {

    private lateinit var hoursPicker: NumberPicker
    private lateinit var minutesPicker: NumberPicker
    private lateinit var applyButton: Button
    private lateinit var switchNotifications: Switch

    private val sharedPrefsName = "settings" // Имя SharedPreferences
    private val switchKey = "notifications_enabled" // Ключ для состояния Switch
    private val hoursKey = "reminder_hours" // Ключ для хранения часов
    private val minutesKey = "reminder_minutes" // Ключ для хранения минут

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setings, container, false)

        // Инициализация элементов
        hoursPicker = view.findViewById(R.id.numberPicker_hours)
        minutesPicker = view.findViewById(R.id.numberPicker_minutes)
        applyButton = view.findViewById(R.id.button_apply_all)
        switchNotifications = view.findViewById(R.id.switch_notifications)

        // Настройка NumberPicker
        hoursPicker.minValue = 0
        hoursPicker.maxValue = 23
        minutesPicker.minValue = 0
        minutesPicker.maxValue = 59

        // Восстановление состояний
        loadSwitchState()
        loadTimePickerValues()

        // Обработка изменения состояния Switch
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveSwitchState(isChecked)
            toggleNotifications(isChecked)
        }

        // Обработка кнопки для применения ко всем записям
        applyButton.setOnClickListener {
            val hours = hoursPicker.value
            val minutes = minutesPicker.value
            applyToAllRecords(hours, minutes)
            saveTimePickerValues(hours, minutes) // Сохраняем время
        }

        return view
    }

    // Сохранение состояния Switch в SharedPreferences
    private fun saveSwitchState(isChecked: Boolean) {
        val sharedPreferences =
            requireContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(switchKey, isChecked).apply()
    }

    // Восстановление состояния Switch из SharedPreferences
    private fun loadSwitchState() {
        val sharedPreferences =
            requireContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val isChecked = sharedPreferences.getBoolean(switchKey, false)
        switchNotifications.isChecked = isChecked
    }

    // Сохранение значений NumberPicker (часы и минуты)
    private fun saveTimePickerValues(hours: Int, minutes: Int) {
        val sharedPreferences =
            requireContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(hoursKey, hours).putInt(minutesKey, minutes).apply()
    }

    // Восстановление значений NumberPicker
    private fun loadTimePickerValues() {
        val sharedPreferences =
            requireContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val savedHours = sharedPreferences.getInt(hoursKey, 0) // По умолчанию 0 часов
        val savedMinutes = sharedPreferences.getInt(minutesKey, 0) // По умолчанию 0 минут

        hoursPicker.value = savedHours
        minutesPicker.value = savedMinutes
    }

    private fun applyToAllRecords(hours: Int, minutes: Int) {
        val newReminderTimeOffsetInMillis = (hours * 60 + minutes) * 60 * 1000
        val db: DataBase = DataBase(requireContext())

        // Получаем все записи из базы данных
        val allEntries = db.getAllEntriesForMes()
        for (entry in allEntries) {
            // Парсим дату и время записи клиента
            val appointmentTime = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .parse("${entry.date} ${entry.time}")?.time ?: continue

            // Вычисляем новое время напоминания
            val newReminderTime = appointmentTime - newReminderTimeOffsetInMillis

            // Убедимся, что напоминание ставится на будущее
            if (newReminderTime > System.currentTimeMillis() && switchNotifications.isChecked) {
                scheduleReminder(requireContext(), entry.name, newReminderTime)
            }
        }

        Toast.makeText(
            requireContext(),
            "Час нагадувань для всіх записів оновлено!",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun toggleNotifications(isEnabled: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(switchKey, isEnabled).apply()

        if (isEnabled) {
            Toast.makeText(requireContext(), "Усі нагадування увімкнені", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Усі нагадування вимкнені", Toast.LENGTH_SHORT).show()
        }
    }






    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleReminder(context: Context, name: String, timeInMillis: Long) {
        val sharedPreferences = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("client_name", name)
            putExtra(
                "client_time",
                SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(timeInMillis)
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timeInMillis.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }


}
