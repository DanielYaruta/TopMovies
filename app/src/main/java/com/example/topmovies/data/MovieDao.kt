package com.example.topmovies.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE page = :page")
    suspend fun getMoviesByPage(page: Int): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%'")
    suspend fun searchMovies(query: String): List<MovieEntity>

    @Query("DELETE FROM movies")
    suspend fun clearAll()
}
