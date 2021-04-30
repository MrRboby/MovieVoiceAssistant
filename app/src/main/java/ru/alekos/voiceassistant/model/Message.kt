package ru.alekos.voiceassistant.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.alekos.voiceassistant.entity.MessageEntity
import ru.alekos.voiceassistant.enumeration.Sender
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Message(var text: String, var date: Date, val sender: Sender) : Parcelable {
    constructor(text: String?, sender: Sender) : this(text ?: "", Date(), sender)
    constructor(entity: MessageEntity) : this(entity.text, SimpleDateFormat().parse(entity.date), Sender.valueOf(entity.sender))
}
