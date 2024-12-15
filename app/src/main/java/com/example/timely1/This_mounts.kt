package com.example.timely1

import EntriesGroupedAdapter
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timely1.DataBase.DataBase
import com.example.timely1.models.Entry
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class This_mounts : Fragment() {


    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_this_mounts, container, false)
        val month:TextView = view.findViewById(R.id.Month)
        val itemsRecycler: RecyclerView = view.findViewById(R.id.items_recycler)
        val db = DataBase(requireContext())

        // Получаем записи из базы данных
        val entries = db.getAllEntries().map {
            Entry(
                id = it["ID"] as Int,
                name = it["client_name"] as String,
                secondName = it["client_second_name"] as String,
                thirdName = it["client_third_name"] as String,
                number = it["client_number"] as Long,
                date = it["client_date"] as String,
                time = it["client_time"] as String,
                price = it["client_price"] as Double,
                additional = it["client_additional"] as String
            )
        }


        val currentDate = LocalDate.now()

// Находим первый день текущего месяца
        val firstDayOfMonth = currentDate.withDayOfMonth(1)

// Находим последний день текущего месяца
        val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())

// Форматируем дату для парсинга
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

// Фильтруем записи, чтобы они попадали в диапазон текущего месяца
        val filteredEntries = entries.filter {
            // Парсим дату записи
            val entryDate = LocalDate.parse(it.date, formatter)

            // Проверяем, попадает ли дата записи в текущий месяц
            entryDate >= firstDayOfMonth && entryDate <= lastDayOfMonth && entryDate >=currentDate
        }

        // Сортируем по дате (если нужно)
        val groupedEntries = filteredEntries.groupBy { it.date }

        // Подготовка списка для адаптера
        val listItems = mutableListOf<Any>()
        groupedEntries.toSortedMap(compareBy { LocalDate.parse(it, formatter) }).forEach { (date, entriesForDate) ->
            listItems.add(date) // Добавляем дату как заголовок
            // Сортируем записи по времени (предполагается, что time — это строка в формате "HH:mm")
            val sortedEntries = entriesForDate.sortedBy { it.time }
            listItems.addAll(sortedEntries) // Добавляем отсортированные записи этой даты
        }

        // Создаем адаптер и связываем с RecyclerView
        val adapter = EntriesGroupedAdapter(
            items = listItems,
            context = requireContext(),
            onDelete = { entry ->

                // Логика удаления клиента
                Toast.makeText(requireContext(), "Удалён: ${entry.name}", Toast.LENGTH_SHORT).show()
            },
            onUpdate = { entry ->
                // Логика обновления клиента
                Toast.makeText(requireContext(), "Обновление: ${entry.name}", Toast.LENGTH_SHORT).show()
            }
        )


        itemsRecycler.layoutManager = LinearLayoutManager(requireContext())
        itemsRecycler.adapter = adapter
        month.text = currentDate.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("uk")).toString().uppercase()

        return view
    }

}