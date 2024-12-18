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

        // Обработка кнопки для применения ко всем записям
        applyButton.setOnClickListener {
            val hours = hoursPicker.value
            val minutes = minutesPicker.value
            applyToAllRecords(hours, minutes)
        }

        return view
    }

    private fun applyToAllRecords(hours: Int, minutes: Int) {
        // Логика сохранения для всех записей
        val newReminderTime = (hours * 60 + minutes) * 60 * 1000 // перевод в миллисекунды
        Toast.makeText(
            requireContext(),
            "Нове нагадування буде за $hours год. $minutes хв.",
            Toast.LENGTH_SHORT
        ).show()

        // Здесь можно сохранить настройки в SharedPreferences или БД
        saveReminderTime(newReminderTime)
    }

    private fun saveReminderTime(timeInMillis: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("reminder_time", timeInMillis).apply()
    }
}
