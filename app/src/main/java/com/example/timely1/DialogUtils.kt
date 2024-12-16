import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isInvisible
import com.example.timely1.R
import com.example.timely1.models.Entry

object DialogUtils {

    fun showClientInfoDialog(context: Context, entry: Entry, onDeleteClick: () -> Unit, onUpdateClick: () -> Unit) {
        // Загружаем разметку диалога
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_client_info, null)

        // Создаём диалог
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Настройка данных в диалоге
        dialogView.findViewById<TextView>(R.id.fullname_dialog).text = "${entry.name} ${entry.secondName} ${entry.thirdName}"
        dialogView.findViewById<TextView>(R.id.number_dialog).text = entry.number.toString()
        dialogView.findViewById<TextView>(R.id.date_dialog).text = entry.date
        dialogView.findViewById<TextView>(R.id.time_dialog).text = entry.time
        dialogView.findViewById<TextView>(R.id.price_dialog).text = "${entry.price} грн"
        dialogView.findViewById<TextView>(R.id.additional_doalog).text = entry.additional

        val donebtn:Button = dialogView.findViewById(R.id.delBtn_dialog)
        if(entry.isDone=="true")
            donebtn.visibility = View.GONE
        else{
        donebtn.setOnClickListener {
            onDeleteClick()
            dialog.dismiss()
        }
        }

        dialogView.findViewById<Button>(R.id.updateBtn_dialog).setOnClickListener {
            onUpdateClick()
            dialog.dismiss()
        }

        // Устанавливаем прозрачный фон для диалога
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Показываем диалог
        dialog.show()
    }
}
