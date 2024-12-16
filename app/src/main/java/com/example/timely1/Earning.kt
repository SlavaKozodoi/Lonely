package com.example.timely1

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.timely1.DataBase.DataBase
import com.example.timely1.models.Entry
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Earning : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val db = DataBase(requireContext())
        val view = inflater.inflate(R.layout.fragment_earning, container, false)


        val today_count:TextView = view.findViewById(R.id.today_count)
        val week_count:TextView = view.findViewById(R.id.week_count)
        val month_count:TextView = view.findViewById(R.id.month_count)
        val kvartal_count:TextView = view.findViewById(R.id. kvartal_count)

        val today_summa:TextView = view.findViewById(R.id.today_summa)
        val week_summa:TextView = view.findViewById(R.id.week_summa)
        val month_summa:TextView = view.findViewById(R.id.month_summa)
        val kvartal_summa:TextView = view.findViewById(R.id.kvartal_summa)





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
                additional = it["client_additional"] as String,
                isDone = it["client_isDone"] as String
            )
        }


        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val todayCount = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate == currentDate && it.isDone == "true"
        }.count()
        today_count.text = todayCount.toString()

        val todaySumma = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate == currentDate && it.isDone == "true"
        }.sumOf { it.price }
        today_summa.text = todaySumma.toString()+ " грн"









        val monday = currentDate.with(DayOfWeek.MONDAY)
        val sunday = currentDate.with(DayOfWeek.SUNDAY)
        val weekCount = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate >= monday && entryDate <= sunday && it.isDone == "true"
        }.count()
        week_count.text = weekCount.toString()

        val weekSumma = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate >= monday && entryDate <= sunday && it.isDone == "true"
        }.sumOf { it.price }
        week_summa.text = weekSumma.toString()+ " грн"




        val firstDayOfMonth = currentDate.withDayOfMonth(1)
        val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())


        val monthCount = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate >= firstDayOfMonth && entryDate <= lastDayOfMonth && it.isDone == "true"
        }.count()
        month_count.text = monthCount.toString()

        val monthSumma = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate >= firstDayOfMonth && entryDate <= lastDayOfMonth && it.isDone == "true"
        }.sumOf { it.price }
        month_summa.text = monthSumma.toString()+ " грн"






        val currentMonth = currentDate.monthValue
        val firstDayOfQuarter: LocalDate
        val lastDayOfQuarter: LocalDate

        when (currentMonth) {
            in 1..3 -> {
                firstDayOfQuarter = LocalDate.of(currentDate.year, 1, 1)
                lastDayOfQuarter = LocalDate.of(currentDate.year, 3, 31)
            }
            in 4..6 -> {
                firstDayOfQuarter = LocalDate.of(currentDate.year, 4, 1)
                lastDayOfQuarter = LocalDate.of(currentDate.year, 6, 30)
            }
            in 7..9 -> {
                firstDayOfQuarter = LocalDate.of(currentDate.year, 7, 1)
                lastDayOfQuarter = LocalDate.of(currentDate.year, 9, 30)
            }
            else -> {
                firstDayOfQuarter = LocalDate.of(currentDate.year, 10, 1)
                lastDayOfQuarter = LocalDate.of(currentDate.year, 12, 31)
            }
        }
        val kvartalCount = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate >= firstDayOfQuarter && entryDate <= lastDayOfQuarter && it.isDone == "true"
        }.count()
        kvartal_count.text = kvartalCount.toString()

        val kvartalSumma = entries.filter {
            val entryDate = LocalDate.parse(it.date, formatter)
            entryDate >= firstDayOfQuarter && entryDate <= lastDayOfQuarter && it.isDone == "true"
        }.sumOf { it.price }
        kvartal_summa.text = kvartalSumma.toString() + " грн"






        return view
    }


}