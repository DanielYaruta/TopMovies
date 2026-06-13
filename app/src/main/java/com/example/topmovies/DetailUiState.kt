package com.example.topmovies

import com.example.topmovies.data.CreditsResponse
import com.example.topmovies.data.Movie
import com.example.topmovies.data.MovieDetails

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(
        val details: MovieDetails? = null,
        val credits: CreditsResponse? = null,
        val similarMovies: List<Movie> = emptyList()
    ) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
