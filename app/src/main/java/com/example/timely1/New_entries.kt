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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.timely1.DataBase.DataBase
import com.example.timely1.Notification.ReminderReceiver
import java.text.SimpleDateFormat
import java.util.Locale

class New_entries : Fragment() {

    private var entryId: Long = -1
    private lateinit var db: DataBase

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_entries, container, false)

        val title:TextView = view.findViewById(R.id.title)
        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextSecondName: EditText = view.findViewById(R.id.editTextSecondName)
        val editTextThirdName: EditText = view.findViewById(R.id.editTextThirdName)
        val editTextPhone: EditText = view.findViewById(R.id.editTextPhone)
        val editTextDate: EditText = view.findViewById(R.id.editTextDate)
        val editTextTime: EditText = view.findViewById(R.id.editTextTime)
        val editTextPrice: EditText = view.findViewById(R.id.editTextPrice)
        val editTextTextAdditional: EditText = view.findViewById(R.id.editTextTextAdditional)
        val buttonAdd: Button = view.findViewById(R.id.Add_btn)
        val buttonDel: Button = view.findViewById(R.id.button_del)
        buttonDel.visibility = View.GONE
        db = DataBase(requireContext())

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
                    editTextDate.setText(entry.date)
                    editTextTime.setText(entry.time)
                    editTextPrice.setText(entry.price.toString())
                    editTextTextAdditional.setText(entry.additional)
                    buttonDel.visibility = View.VISIBLE
                    buttonAdd.text = "Оновити"
                    title.text = R.string.update_dialog.toString()
                }
            }
        }

        buttonAdd.setOnClickListener {
            val name = "${editTextName.text} ${editTextSecondName.text}"
            val time = "${editTextDate.text} ${editTextTime.text}"

            if (entryId == -1L) {
                // Добавление новой записи
                db.insertData(
                    editTextName.text.toString(),
                    editTextSecondName.text.toString(),
                    editTextThirdName.text.toString(),
                    editTextPhone.text.toString().toLong(),
                    editTextDate.text.toString(),
                    editTextTime.text.toString(),
                    editTextPrice.text.toString().toDouble(),
                    editTextTextAdditional.text.toString()
                )
                scheduleReminder(requireContext(), name, time)
                Toast.makeText(requireContext(), "Додано!", Toast.LENGTH_SHORT).show()
            } else {
                db.updateData(
                    entryId,
                    editTextName.text.toString(),
                    editTextSecondName.text.toString(),
                    editTextThirdName.text.toString(),
                    editTextPhone.text.toString().toLong(),
                    editTextDate.text.toString(),
                    editTextTime.text.toString(),
                    editTextPrice.text.toString().toDouble(),
                    editTextTextAdditional.text.toString()
                )
                scheduleReminder(requireContext(), name, time)
                Toast.makeText(requireContext(), "Оновлено!", Toast.LENGTH_SHORT).show()
            }

            requireActivity().supportFragmentManager.popBackStack()
        }

        buttonDel.setOnClickListener{
            db.deleteData(entryId.toInt())
            Toast.makeText(requireContext(), "Видалено!", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleReminder(context: Context, name: String, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("client_name", name)
            putExtra("client_time", time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            time.hashCode(), // Уникальный код для каждого напоминания
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Формат даты для парсинга входных данных
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val appointmentTime = dateFormat.parse(time)?.time ?: return

        // Получение пользовательских настроек
        val sharedPref = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        val reminderHours = sharedPref.getInt("reminder_hours", 1) // По умолчанию 1 час
        val reminderMinutes = sharedPref.getInt("reminder_minutes", 0) // По умолчанию 0 минут

        // Рассчитываем время уведомления
        val reminderTime = appointmentTime - (reminderHours * 60 * 60 * 1000) - (reminderMinutes * 60 * 1000)

        // Запланировать уведомление
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }


}


