package com.example.topmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovies.data.Movie
import com.example.topmovies.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieListViewModel : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 0
    private var totalPages = 1

    init { loadNextPage() }

    fun loadNextPage() {
        if (_isLoading.value || currentPage >= totalPages) return
        load(currentPage + 1)
    }

    fun refresh() {
        if (_isLoading.value) return
        _isRefreshing.value = true
        _movies.value = emptyList()
        currentPage = 0
        totalPages = 1
        load(1)
    }

    fun clearError() { _error.value = null }

    private fun load(page: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                val response = ApiClient.service.getTopRatedMovies(
                    BuildConfig.TMDB_API_KEY, "en-US", page
                )
                currentPage = response.page
                totalPages = response.totalPages
                _movies.value = _movies.value + response.results
            }.onFailure { e ->
                _error.value = e.message
            }
            _isLoading.value = false
            _isRefreshing.value = false
        }
    }
}
