package com.example.timely1.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timely1.R
import com.example.timely1.models.Entry

class EntriesAdapter(
    private val entries: List<Entry>,
    private val onInfoClick: (Entry) -> Unit
) : RecyclerView.Adapter<EntriesAdapter.EntryViewHolder>() {

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.client_fullname_textView)
        val clientTime: TextView = itemView.findViewById(R.id.client_time_textView)
        val clientPrice: TextView = itemView.findViewById(R.id.client_price_textView)
        val buttonInfo: ImageView = itemView.findViewById(R.id.image_isDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.client_item_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.clientName.text = "${entry.name} ${entry.secondName} ${entry.thirdName}"
        holder.clientTime.text = entry.time
        holder.clientPrice.text = entry.price.toString()

        holder.buttonInfo.setOnClickListener {
            onInfoClick(entry)
        }
    }

    override fun getItemCount(): Int {
        return entries.size
    }
}
