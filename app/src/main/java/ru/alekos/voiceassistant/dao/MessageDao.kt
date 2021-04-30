package ru.alekos.voiceassistant.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.alekos.voiceassistant.entity.MessageEntity

@Dao
interface MessageDao {
    @Insert
    fun insertAll(messages: List<MessageEntity>)

    @Query("SELECT id, text, date, sender FROM messages ORDER BY date")
    fun getAllMessages(): List<MessageEntity>

    @Query("DELETE FROM messages")
    fun deleteAll(): Int
}