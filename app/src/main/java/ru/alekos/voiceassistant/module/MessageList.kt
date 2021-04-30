package ru.alekos.voiceassistant.module

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.alekos.voiceassistant.model.Message

class MessageList(private val recyclerView: RecyclerView, context: Context) {
    private var messageListAdapter: MessageListAdapter = MessageListAdapter()

    var messageList: MutableList<Message>
        get() {
            return messageListAdapter.messageList
        }
        set(value) {
            messageListAdapter.messageList = value
        }

    init {
        this.recyclerView.layoutManager = LinearLayoutManager(context)
        this.recyclerView.adapter = messageListAdapter
    }

    fun scrollToBottom() {
        if (messageListAdapter.messageList.isNotEmpty()) {
            recyclerView.scrollToPosition(messageListAdapter.messageList.size - 1)
        }
    }

    fun notifyDataSetChanged() {
        messageListAdapter.notifyDataSetChanged()
    }
}