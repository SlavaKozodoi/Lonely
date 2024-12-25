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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timely1.DataBase.DataBase
import com.example.timely1.models.Entry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class All_entries : Fragment() {

    @SuppressLint("MissingInflatedId")
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
                number = it["client_number"] as String,
                date = it["client_date"] as String,
                time = it["client_time"] as String,
                price = it["client_price"] as Double,
                additional = it["client_additional"] as String,
                isDone = it["client_isDone"] as String
            )
        }

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val groupedEntries = entries.groupBy { it.date }

        val listItems = mutableListOf<Any>()
        val dateList = mutableListOf<String>()

        groupedEntries.toSortedMap(compareBy { LocalDate.parse(it, formatter) }).forEach { (date, entriesForDate) ->
            listItems.add(date)
            dateList.add(date) // Collect the dates for fast scrolling
            val sortedEntries = entriesForDate.sortedBy { it.time }
            listItems.addAll(sortedEntries)
        }
        val nonitems: TextView = view.findViewById(R.id.textView)
        if(groupedEntries.isEmpty())
            nonitems.visibility = View.VISIBLE
        else
            nonitems.visibility = View.GONE

        val adapter = EntriesGroupedAdapter(
            items = listItems,
            context = requireContext(),
            onDelete = { entry ->
                Toast.makeText(requireContext(), "Удалён: ${entry.name}", Toast.LENGTH_SHORT).show()
            },
            onUpdate = { entry ->
                Toast.makeText(requireContext(), "Обновление: ${entry.name}", Toast.LENGTH_SHORT).show()
            }
        )

        itemsRecycler.layoutManager = LinearLayoutManager(requireContext())
        itemsRecycler.adapter = adapter

        // Attach the fast scroller and set date list
        val fastScroller: CustomFastScroller = view.findViewById(R.id.fastScroller)
        fastScroller.setDateList(dateList)
        fastScroller.attachToRecyclerView(itemsRecycler)

        return view
    }
}


