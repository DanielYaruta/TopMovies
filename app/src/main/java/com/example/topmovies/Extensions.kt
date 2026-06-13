package com.example.topmovies

fun String?.tmdbImageUrl(size: String = "w185") =
    "https://image.tmdb.org/t/p/$size${this.orEmpty()}"

fun Double.toRatingText() = "★ ${"%.1f".format(this)}"
