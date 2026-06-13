package com.example.topmovies

import com.example.topmovies.data.Movie

sealed class MovieListUiState {
    object Loading : MovieListUiState()
    data class Success(
        val movies: List<Movie>,
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false
    ) : MovieListUiState()
}
