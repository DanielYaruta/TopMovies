package com.example.topmovies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovies.data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieListViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MovieRepository(app)

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 0
    private var totalPages = 1
    private var isLoading = false

    private var isSearchActive = false
    private var savedStateBeforeSearch: MovieListUiState? = null

    init { load(1) }

    fun loadNextPage() {
        if (isSearchActive || isLoading || currentPage >= totalPages) return
        val movies = (_uiState.value as? MovieListUiState.Success)?.movies ?: emptyList()
        _uiState.value = if (movies.isEmpty()) MovieListUiState.Loading
                         else MovieListUiState.Success(movies, isLoadingMore = true)
        load(currentPage + 1, movies)
    }

    fun refresh() {
        if (isLoading) return
        isLoading = true
        isSearchActive = false
        savedStateBeforeSearch = null
        currentPage = 0
        totalPages = 1
        _uiState.value = MovieListUiState.Success(emptyList(), isRefreshing = true)
        viewModelScope.launch {
            repository.clearCache()
            isLoading = false
            load(1)
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            if (isSearchActive) {
                isSearchActive = false
                savedStateBeforeSearch?.let { _uiState.value = it }
                savedStateBeforeSearch = null
            }
            return
        }
        if (!isSearchActive) {
            isSearchActive = true
            savedStateBeforeSearch = _uiState.value
        }
        viewModelScope.launch {
            val results = repository.searchMovies(query)
            _uiState.value = MovieListUiState.Success(results)
        }
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
