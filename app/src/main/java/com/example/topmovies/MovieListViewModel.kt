package com.example.topmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovies.data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieListViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 0
    private var totalPages = 1
    private var isLoading = false

    init { load(1) }

    fun loadNextPage() {
        if (isLoading || currentPage >= totalPages) return
        val movies = (_uiState.value as? MovieListUiState.Success)?.movies ?: emptyList()
        _uiState.value = if (movies.isEmpty()) MovieListUiState.Loading
                         else MovieListUiState.Success(movies, isLoadingMore = true)
        load(currentPage + 1, movies)
    }

    fun refresh() {
        if (isLoading) return
        currentPage = 0
        totalPages = 1
        _uiState.value = MovieListUiState.Success(emptyList(), isRefreshing = true)
        load(1)
    }

    fun clearError() { _error.value = null }

    private fun load(page: Int, existing: List<Movie> = emptyList()) {
        isLoading = true
        viewModelScope.launch {
            runCatching {
                val response = repository.getTopRatedMovies(page)
                currentPage = response.page
                totalPages = response.totalPages
                _uiState.value = MovieListUiState.Success(existing + response.results)
            }.onFailure { e ->
                _error.value = e.message ?: "Failed to load movies"
                _uiState.value = if (existing.isEmpty()) MovieListUiState.Loading
                                 else MovieListUiState.Success(existing)
            }
            isLoading = false
        }
    }
}
