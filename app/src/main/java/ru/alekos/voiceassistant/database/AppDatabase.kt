package ru.alekos.voiceassistant.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.alekos.voiceassistant.dao.MessageDao
import ru.alekos.voiceassistant.dao.MovieDao
import ru.alekos.voiceassistant.entity.MessageEntity
import ru.alekos.voiceassistant.entity.MovieEntity

@Database(entities = [MessageEntity::class, MovieEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMessageDao(): MessageDao
    abstract fun getMovieDao(): MovieDao
}