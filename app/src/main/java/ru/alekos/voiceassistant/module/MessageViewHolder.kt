package ru.alekos.voiceassistant.module

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.alekos.voiceassistant.R
import ru.alekos.voiceassistant.model.Message
import java.text.DateFormat
import java.text.SimpleDateFormat

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var messageText: TextView = itemView.findViewById(R.id.messageTextView)
    private var messageDate: TextView = itemView.findViewById(R.id.messageDateView)

    fun bind(message: Message) {
        messageText.text = message.text
        messageDate.text = SimpleDateFormat().format(message.date)
    }
}