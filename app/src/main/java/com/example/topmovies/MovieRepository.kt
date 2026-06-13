package com.example.topmovies

import android.content.Context
import com.example.topmovies.data.AppDatabase
import com.example.topmovies.data.Movie
import com.example.topmovies.data.MovieResponse
import com.example.topmovies.data.toEntity
import com.example.topmovies.data.toMovie
import com.example.topmovies.network.ApiClient

class MovieRepository(context: Context) {
    private val api = ApiClient.service
    private val dao = AppDatabase.getInstance(context).movieDao()
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    suspend fun getTopRatedMovies(page: Int): MovieResponse {
        val cached = dao.getMoviesByPage(page)
        if (cached.isNotEmpty()) {
            val totalPages = prefs.getInt("total_pages", page)
            return MovieResponse(
                page = page,
                results = cached.map { it.toMovie() },
                totalPages = totalPages,
                totalResults = cached.size
            )
        }
        val response = api.getTopRatedMovies(BuildConfig.TMDB_API_KEY, "en-US", page)
        dao.insertMovies(response.results.map { it.toEntity(page) })
        prefs.edit().putInt("total_pages", response.totalPages).apply()
        return response
    }

    suspend fun searchMovies(query: String): List<Movie> =
        dao.searchMovies(query).map { it.toMovie() }

    suspend fun clearCache() = dao.clearAll()

    suspend fun getMovieDetails(movieId: Int) =
        api.getMovieDetails(movieId, BuildConfig.TMDB_API_KEY)

    suspend fun getCredits(movieId: Int) =
        api.getCredits(movieId, BuildConfig.TMDB_API_KEY)

    suspend fun getSimilarMovies(movieId: Int) =
        api.getSimilarMovies(movieId, BuildConfig.TMDB_API_KEY)
}
