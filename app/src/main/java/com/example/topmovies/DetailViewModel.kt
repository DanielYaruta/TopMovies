package com.example.topmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovies.data.CreditsResponse
import com.example.topmovies.data.Movie
import com.example.topmovies.data.MovieDetails
import com.example.topmovies.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _details = MutableStateFlow<MovieDetails?>(null)
    val details: StateFlow<MovieDetails?> = _details.asStateFlow()

    private val _credits = MutableStateFlow<CreditsResponse?>(null)
    val credits: StateFlow<CreditsResponse?> = _credits.asStateFlow()

    private val _similarMovies = MutableStateFlow<List<Movie>>(emptyList())
    val similarMovies: StateFlow<List<Movie>> = _similarMovies.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var loadedMovieId = -1

    fun clearError() { _error.value = null }

    fun load(movieId: Int) {
        if (movieId == loadedMovieId) return
        loadedMovieId = movieId
        viewModelScope.launch {
            launch {
                runCatching {
                    _details.value = ApiClient.service.getMovieDetails(movieId, BuildConfig.TMDB_API_KEY)
                }.onFailure { _error.value = it.message ?: "Failed to load movie details" }
            }
            launch {
                runCatching {
                    _credits.value = ApiClient.service.getCredits(movieId, BuildConfig.TMDB_API_KEY)
                }.onFailure { _error.value = it.message ?: "Failed to load credits" }
            }
            launch {
                runCatching {
                    _similarMovies.value = ApiClient.service
                        .getSimilarMovies(movieId, BuildConfig.TMDB_API_KEY)
                        .results
                }
            }
        }
    }
}
