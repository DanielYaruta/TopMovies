package com.example.topmovies

import com.example.topmovies.network.ApiClient

class MovieRepository {
    private val api = ApiClient.service

    suspend fun getTopRatedMovies(page: Int) =
        api.getTopRatedMovies(BuildConfig.TMDB_API_KEY, "en-US", page)

    suspend fun getMovieDetails(movieId: Int) =
        api.getMovieDetails(movieId, BuildConfig.TMDB_API_KEY)

    suspend fun getCredits(movieId: Int) =
        api.getCredits(movieId, BuildConfig.TMDB_API_KEY)

    suspend fun getSimilarMovies(movieId: Int) =
        api.getSimilarMovies(movieId, BuildConfig.TMDB_API_KEY)
}
