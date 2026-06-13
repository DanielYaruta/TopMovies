package com.example.topmovies

import android.content.Context
import com.example.topmovies.data.AppDatabase
import com.example.topmovies.data.Movie
import com.example.topmovies.data.MovieResponse
import com.example.topmovies.data.toEntity
import com.example.topmovies.data.toMovie
import com.example.topmovies.network.ApiClient
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class MovieRepository(context: Context) {
    private val api = ApiClient.service
    private val dao = AppDatabase.getInstance(context).movieDao()
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getTopRatedMovies(page: Int): Single<MovieResponse> =
        dao.getMoviesByPage(page)
            .flatMap { cached ->
                if (cached.isNotEmpty()) {
                    val totalPages = prefs.getInt("total_pages", page)
                    Single.just(MovieResponse(
                        page = page,
                        results = cached.map { it.toMovie() },
                        totalPages = totalPages,
                        totalResults = cached.size
                    ))
                } else {
                    api.getTopRatedMovies(BuildConfig.TMDB_API_KEY, "en-US", page)
                        .doOnSuccess { response ->
                            prefs.edit().putInt("total_pages", response.totalPages).apply()
                        }
                        .flatMap { response ->
                            dao.insertMovies(response.results.map { it.toEntity(page) })
                                .andThen(Single.just(response))
                        }
                }
            }

    fun searchMovies(query: String): Single<List<Movie>> =
        dao.searchMovies(query).map { list -> list.map { it.toMovie() } }

    fun clearCache(): Completable = dao.clearAll()

    fun getMovieDetails(movieId: Int) =
        api.getMovieDetails(movieId, BuildConfig.TMDB_API_KEY)

    fun getCredits(movieId: Int) =
        api.getCredits(movieId, BuildConfig.TMDB_API_KEY)

    fun getSimilarMovies(movieId: Int) =
        api.getSimilarMovies(movieId, BuildConfig.TMDB_API_KEY)
}
