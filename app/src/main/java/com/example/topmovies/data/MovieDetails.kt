package com.example.topmovies.data

data class MovieDetails(
    val id: Int,
    val runtime: Int?,
    val genres: List<Genre>
)
