package com.example.topmovies.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE page = :page")
    fun getMoviesByPage(page: Int): Single<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(movies: List<MovieEntity>): Completable

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%'")
    fun searchMovies(query: String): Single<List<MovieEntity>>

    @Query("DELETE FROM movies")
    fun clearAll(): Completable
}
