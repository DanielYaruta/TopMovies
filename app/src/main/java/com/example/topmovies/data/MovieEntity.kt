package com.example.topmovies.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val voteAverage: Double,
    val releaseDate: String,
    val posterPath: String?,
    val backdropPath: String?,
    val page: Int
)

fun MovieEntity.toMovie() = Movie(id, title, overview, voteAverage, releaseDate, posterPath, backdropPath)

fun Movie.toEntity(page: Int) = MovieEntity(id, title, overview, voteAverage, releaseDate, posterPath, backdropPath, page)
