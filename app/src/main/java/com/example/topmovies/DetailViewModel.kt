package com.example.topmovies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MovieRepository(app)

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var loadedMovieId = -1

    fun load(movieId: Int) {
        if (movieId == loadedMovieId) return
        loadedMovieId = movieId
        viewModelScope.launch {
            runCatching {
                repository.getMovieDetails(movieId)
            }.onSuccess { details ->
                _uiState.value = DetailUiState.Success(details = details)
                launch {
                    runCatching {
                        val credits = repository.getCredits(movieId)
                        (_uiState.value as? DetailUiState.Success)?.let {
                            _uiState.value = it.copy(credits = credits)
                        }
                    }
                }
                launch {
                    runCatching {
                        val similar = repository.getSimilarMovies(movieId).results
                        (_uiState.value as? DetailUiState.Success)?.let {
                            _uiState.value = it.copy(similarMovies = similar)
                        }
                    }
                }
            }.onFailure {
                _uiState.value = DetailUiState.Error(it.message ?: "Failed to load movie details")
            }
        }
    }
}
