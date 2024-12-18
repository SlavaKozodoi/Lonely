import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.timely1.DataBase.DataBase
import com.example.timely1.R
import com.example.timely1.models.Entry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EntriesGroupedAdapter(
    private val items: MutableList<Any>, // Сделано изменяемым списком
    private val context: Context,
    private val onDelete: (Entry) -> Unit,
    private val onUpdate: (Entry) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ENTRY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> TYPE_HEADER
            is Entry -> TYPE_ENTRY
            else -> throw IllegalArgumentException("Неизвестный тип элемента")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_ENTRY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.client_item_entry, parent, false)
                EntryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Неизвестный viewType")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is String -> (holder as DateHeaderViewHolder).bind(item)
            is Entry -> (holder as EntryViewHolder).bind(item, context, onDelete, onUpdate, this)  // Передаем адаптер
        }
    }

    override fun getItemCount(): Int = items.size

    // Метод для обновления элемента
    fun updateItem(updatedEntry: Entry) {
        val position = items.indexOfFirst { it is Entry && it.id == updatedEntry.id }
        if (position != -1) {
            (items as MutableList<Any>)[position] = updatedEntry // Обновляем элемент
            notifyItemChanged(position) // Обновляем только этот элемент
        }
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(date: String) {
            dateTextView.text = date
        }
    }

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clientName: TextView = itemView.findViewById(R.id.client_fullname_textView)
        private val clientTime: TextView = itemView.findViewById(R.id.client_time_textView)
        private val clientPrice: TextView = itemView.findViewById(R.id.client_price_textView)
        private val buttonInfo: CardView = itemView.findViewById(R.id.cardView)
        private val isDoneIcon: ImageView = itemView.findViewById(R.id.image_isDone)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(
            entry: Entry,
            context: Context,
            onDelete: (Entry) -> Unit,
            onUpdate: (Entry) -> Unit,
            adapter: EntriesGroupedAdapter  // Передаем адаптер сюда
        ) {
            clientName.text = "${entry.name} ${entry.secondName}"
            clientTime.text = entry.time
            clientPrice.text = "${entry.price} грн"
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val entryDate = LocalDate.parse(entry.date, formatter)
            if (entry.isDone == "true")
                isDoneIcon.setImageResource(R.drawable.check)
            else if (entry.isDone == "false" && entryDate < currentDate)
                isDoneIcon.setImageResource(R.drawable.questionicon)

            buttonInfo.setOnClickListener {
                // Показ всплывающего окна через DialogUtils
                DialogUtils.showClientInfoDialog(
                    context = context,  // Контекст текущей Activity или Fragment
                    entry = entry,   // Текущая запись Entry
                    onDeleteClick = { entry ->
                        // Логика удаления записи
                        val db: DataBase = DataBase(context)
                        db.updateIsDone(entry.id.toLong(), true)

                        // Обновляем элемент в адаптере
                        val updatedEntry = entry.copy(isDone = "true")
                        adapter.updateItem(updatedEntry)  // Вызов метода обновления элемента
                    },
                    onUpdateClick = { entry ->
                        // Логика для перехода на экран редактирования записи
                        val navController = (context as AppCompatActivity).findNavController(R.id.fragmentContainerView)
                        val bundle = Bundle().apply {
                            putLong("entry_id", entry.id.toLong())  // Передаем ID записи для редактирования
                        }
                        navController.navigate(R.id.New_entries, bundle)
                    }
                )
            }
        }
    }
}




