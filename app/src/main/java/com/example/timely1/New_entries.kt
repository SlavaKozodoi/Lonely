package com.example.timely1

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.timely1.DataBase.DataBase
import com.example.timely1.Notification.ReminderReceiver
import java.text.SimpleDateFormat
import java.util.*

class New_entries : Fragment() {

    private var entryId: Long = -1
    private lateinit var db: DataBase

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_entries, container, false)

        val title: TextView = view.findViewById(R.id.title)
        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextSecondName: EditText = view.findViewById(R.id.editTextSecondName)
        val editTextThirdName: EditText = view.findViewById(R.id.editTextThirdName)
        val editTextPhone: EditText = view.findViewById(R.id.editTextPhone)
        val textViewDate: TextView = view.findViewById(R.id.editTextDate)
        val textViewTime: TextView = view.findViewById(R.id.editTextTime)
        val editTextPrice: EditText = view.findViewById(R.id.editTextPrice)
        val editTextTextAdditional: EditText = view.findViewById(R.id.editTextTextAdditional)
        val buttonAdd: Button = view.findViewById(R.id.Add_btn)
        val buttonDel: Button = view.findViewById(R.id.button_del)
        buttonDel.visibility = View.GONE
        db = DataBase(requireContext())

        // Установка обработчиков кликов для выбора даты и времени
        textViewDate.setOnClickListener {
            showDatePickerDialog(textViewDate)
        }

        textViewTime.setOnClickListener {
            showTimePickerDialog(textViewTime)
        }

        // Проверяем наличие переданного ID из Bundle
        arguments?.let {
            entryId = it.getLong("entry_id", -1)
            if (entryId != -1L) {
                val entry = db.getEntryById(entryId)
                entry?.let {
                    editTextName.setText(entry.name)
                    editTextSecondName.setText(entry.secondName)
                    editTextThirdName.setText(entry.thirdName)
                    editTextPhone.setText(entry.number.toString())
                    textViewDate.text = entry.date
                    textViewTime.text = entry.time
                    editTextPrice.setText(entry.price.toString())
                    editTextTextAdditional.setText(entry.additional)
                    buttonDel.visibility = View.VISIBLE
                    buttonAdd.text = "Оновити"
                    title.text = getString(R.string.update_dialog)
                }
            }
        }

        buttonAdd.setOnClickListener {
            val name = "${editTextName.text} ${editTextSecondName.text}"
            var time = "${textViewDate.text} ${textViewTime.text}"

            // Проверка на обязательные поля
            if (editTextName.text.isEmpty()) {
                Toast.makeText(requireContext(), "Ім'я обов'язкове!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка на корректность даты
            val dateText = textViewDate.text.toString()
            if (dateText.isEmpty() || !isValidDate(dateText)) {
                Toast.makeText(requireContext(), "Дата не може бути пустою чи раніше чим сьогодні!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка на пустое время
            val timeText = textViewTime.text.toString()
            if (timeText.isEmpty()) {
                Toast.makeText(requireContext(), "Час обов'язковий!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (editTextPrice.text.isEmpty()) {
                Toast.makeText(requireContext(), "Ціна обов'язкова!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Преобразуем цену в Double с проверкой
            val price = try {
                editTextPrice.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Ціна повина бути числом!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Если entryId == -1L, добавляем новую запись
            if (entryId == -1L) {
                db.insertData(
                    editTextName.text.toString(),
                    editTextSecondName.text.toString(),
                    editTextThirdName.text.toString(),
                    editTextPhone.text.toString(),
                    dateText,
                    timeText,
                    price,
                    editTextTextAdditional.text.toString()
                )
                scheduleReminder(requireContext(), name, time)
                Toast.makeText(requireContext(), "Додано!", Toast.LENGTH_SHORT).show()
            } else {
                // Обновление существующей записи
                db.updateData(
                    entryId,
                    editTextName.text.toString(),
                    editTextSecondName.text.toString(),
                    editTextThirdName.text.toString(),
                    editTextPhone.text.toString(),
                    dateText,
                    timeText,
                    price,
                    editTextTextAdditional.text.toString()
                )
                scheduleReminder(requireContext(), name, time)
                Toast.makeText(requireContext(), "Оновлено!", Toast.LENGTH_SHORT).show()
            }

            requireActivity().supportFragmentManager.popBackStack()
        }




        buttonDel.setOnClickListener {
            db.deleteData(entryId.toInt())
            Toast.makeText(requireContext(), "Видалено!", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Если дата не выбрана, установим сегодняшнюю дату
        if (textView.text.isEmpty()) {
            val selectedDate = String.format("%02d.%02d.%d", day, month + 1, year)
            textView.text = selectedDate
        }

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d.%02d.%d", dayOfMonth, month + 1, year)
            textView.text = selectedDate
        }, year, month, day).show()
    }



    private fun showTimePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()

        // Получаем текущее время из textView, если оно уже установлено
        val currentTime = if (textView.text.isNotEmpty()) {
            val timeString = textView.text.toString()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.parse(timeString)
        } else {
            null
        }

        // Если время указано в textView, используем его. В противном случае — текущее время
        if (currentTime != null) {
            calendar.time = currentTime
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            textView.text = selectedTime
        }, hour, minute, true).show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleReminder(context: Context, name: String, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("client_name", name)
            putExtra("client_time", time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            time.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val appointmentTime = dateFormat.parse(time)?.time ?: return

        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val reminderHours = sharedPref.getInt("reminder_hours", 1)
        val reminderMinutes = sharedPref.getInt("reminder_minutes", 0)

        val reminderTime = appointmentTime - (reminderHours * 60 * 60 * 1000) - (reminderMinutes * 60 * 1000)

        if (reminderTime <= System.currentTimeMillis()) {
            Toast.makeText(context, "Час для нагадування вже минув!", Toast.LENGTH_SHORT).show()
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }
    private fun isValidDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()

        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        val selectedDate = dateFormat.parse(date)

        return selectedDate != null && !selectedDate.before(currentDate.time)
    }
}
