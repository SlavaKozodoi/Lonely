package com.example.timely1

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.timely1.DataBase.DataBase

class New_entries : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_new_entries, container, false)


        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextSecondName: EditText = view.findViewById(R.id.editTextSecondName)
        val editTextThirdName: EditText = view.findViewById(R.id.editTextThirdName)
        val editTextPhone: EditText = view.findViewById(R.id.editTextPhone)
        val editTextDate: EditText = view.findViewById(R.id.editTextDate)
        val editTextTime: EditText = view.findViewById(R.id.editTextTime)
        val editTextPrice: EditText = view.findViewById(R.id.editTextPrice)
        val editTextTextAdditional: EditText = view.findViewById(R.id.editTextTextAdditional)
        val button: Button = view.findViewById(R.id.Add_btn)


        val db = DataBase(requireContext())

        button.setOnClickListener {
            try {
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
                Toast.makeText(requireContext(), "Добавлено!", Toast.LENGTH_SHORT).show()

                editTextName.text.clear()
                editTextSecondName.text.clear()
                editTextThirdName.text.clear()
                editTextPhone.text.clear()
                editTextDate.text.clear()
                editTextTime.text.clear()
                editTextPrice.text.clear()
                editTextTextAdditional.text.clear()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        val entries = db.getAllEntries()

        if (entries.isNotEmpty()) {
            for (entry in entries) {
                android.util.Log.d("DatabaseEntry", "Запись: $entry")
            }
        } else {
            android.util.Log.d("DatabaseEntry", "База данных пуста!")
        }



        return view
    }
}
