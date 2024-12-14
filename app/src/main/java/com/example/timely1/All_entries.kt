package com.example.timely1

import EntriesGroupedAdapter
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timely1.DataBase.DataBase
import com.example.timely1.models.Entry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class All_entries : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_entries, container, false)

        val itemsRecycler: RecyclerView = view.findViewById(R.id.items_recycler)
        val db = DataBase(requireContext())

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

        Log.d("DatabaseDebug", "Получено записей: ${entries.size}")

        // Текущая дата
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        // Фильтруем записи, начиная с текущего дня
        val filteredEntries = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter) // Парсим дату
            entryDate >= currentDate // Сравниваем с текущей датой
        }

        // Логируем количество записей после фильтрации
        Log.d("DatabaseDebug", "Отфильтровано записей: ${filteredEntries.size}")

        // Группируем записи по дате
        val groupedEntries = filteredEntries.groupBy { it.date }

        // Логируем сгруппированные записи
        Log.d("DatabaseDebug", "Группировка по дате: $groupedEntries")

        val listItems = mutableListOf<Any>()

        // Сортируем по дате
        groupedEntries.toSortedMap(compareBy { LocalDate.parse(it, formatter) }).forEach { (date, entriesForDate) ->
            listItems.add(date) // Добавляем дату как заголовок
            // Сортируем записи по времени (предполагается, что time — это строка в формате "HH:mm")
            val sortedEntries = entriesForDate.sortedBy { it.time }
            listItems.addAll(sortedEntries) // Добавляем отсортированные записи этой даты
        }

        // Логируем итоговый список
        Log.d("DatabaseDebug", "Формированный список элементов: $listItems")

        // Устанавливаем адаптер для RecyclerView
        val adapter = EntriesGroupedAdapter(listItems) { entry ->
            // Обработка клика по записи
            android.widget.Toast.makeText(
                context,
                "Доп. информация для ${entry.name} ${entry.id}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        itemsRecycler.layoutManager = LinearLayoutManager(requireContext())
        itemsRecycler.adapter = adapter

        return view
    }
}
