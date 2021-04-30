package ru.alekos.voiceassistant.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.alekos.voiceassistant.model.Message
import java.text.SimpleDateFormat

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: Int?,
    var text: String,
    var date: String,
    var sender: String
) {
    constructor(message: Message) : this(
        null,
        message.text,
        SimpleDateFormat().format(message.date),
        message.sender.name
    )
}
