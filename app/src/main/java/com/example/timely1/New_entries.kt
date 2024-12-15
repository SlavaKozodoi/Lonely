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

    private var entryId: Long = -1
    private lateinit var db: DataBase

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
        val buttonAdd: Button = view.findViewById(R.id.Add_btn)
        val buttonDel: Button = view.findViewById(R.id.button_del)

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
                    buttonAdd.text = "Обновить"
                }
            }
        }

        buttonAdd.setOnClickListener {
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
                Toast.makeText(requireContext(), "Добавлено!", Toast.LENGTH_SHORT).show()
            } else {
                // Обновление существующей записи
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
                Toast.makeText(requireContext(), "Обновлено!", Toast.LENGTH_SHORT).show()
            }

            // После сохранения данных закрываем фрагмент
            requireActivity().supportFragmentManager.popBackStack()
        }

        buttonDel.setOnClickListener{
            db.deleteData(entryId.toInt())
            Toast.makeText(requireContext(), "Видалено!", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}


