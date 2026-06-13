package com.example.topmovies.data

data class CreditsResponse(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>
)
