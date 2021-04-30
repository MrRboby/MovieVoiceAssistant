package ru.alekos.voiceassistant.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "tmdb_id") val tmdbId: Int,
    val title: String,
    var score: Int
    )
