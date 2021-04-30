package ru.alekos.voiceassistant.module

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.alekos.voiceassistant.R
import ru.alekos.voiceassistant.enumeration.Sender
import ru.alekos.voiceassistant.model.Message
import java.util.*

internal class MessageListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messageList: MutableList<Message> = ArrayList<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == Sender.User.ordinal) {
            view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.user_message, parent, false)
        } else {
            view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.assistant_message, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageViewHolder).bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(index: Int): Int {
        return messageList[index].sender.ordinal
    }
}
