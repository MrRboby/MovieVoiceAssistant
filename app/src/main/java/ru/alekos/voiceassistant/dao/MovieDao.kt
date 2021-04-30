package ru.alekos.voiceassistant.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.alekos.voiceassistant.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert
    fun insert(movie: MovieEntity)

    @Query("SELECT id, title, tmdb_id, score FROM movies")
    fun getMovies(): List<MovieEntity>

    @Query("SELECT id, title, tmdb_id, score FROM movies WHERE score >= 7 ORDER BY score DESC, id DESC")
    fun getFavoriteMoviesOrderedByRating(): List<MovieEntity>
}