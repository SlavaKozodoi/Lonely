package com.example.timely1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

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
            val message = if (isChecked) "Повідомлення увімкнено" else "Повідомлення вимкнено"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
        val newReminderTime = (hours * 60 + minutes) * 60 * 1000 // перевод в миллисекунды
        Toast.makeText(
            requireContext(),
            "Нове нагадування буде за $hours год. $minutes хв.",
            Toast.LENGTH_SHORT
        ).show()
        saveReminderTime(newReminderTime)
    }

    private fun saveReminderTime(timeInMillis: Int) {
        val sharedPreferences =
            requireContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("reminder_time", timeInMillis).apply()
    }
}


