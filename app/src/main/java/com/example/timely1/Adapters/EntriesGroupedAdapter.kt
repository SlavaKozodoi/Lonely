import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timely1.R
import com.example.timely1.models.Entry

class EntriesGroupedAdapter(
    private val items: List<Any>,
    private val onInfoClick: (Entry) -> Unit
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is String -> (holder as DateHeaderViewHolder).bind(item)
            is Entry -> (holder as EntryViewHolder).bind(item, onInfoClick) // Передаем коллбек
        }
    }

    override fun getItemCount(): Int = items.size

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
        private val buttonInfo: Button = itemView.findViewById(R.id.button_dop_info)

        fun bind(entry: Entry, onInfoClick: (Entry) -> Unit) {
            clientName.text = "${entry.name} ${entry.secondName} ${entry.thirdName}"
            clientTime.text = entry.time
            clientPrice.text = "${entry.price} грн"

            buttonInfo.setOnClickListener {
                onInfoClick(entry)
            }
        }
    }
}

