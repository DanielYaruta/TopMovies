package com.example.topmovies.network

import com.example.topmovies.data.CreditsResponse
import com.example.topmovies.data.MovieDetails
import com.example.topmovies.data.MovieResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<MovieResponse>

    @GET("movie/{id}")
    fun getMovieDetails(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Single<MovieDetails>

    @GET("movie/{id}/credits")
    fun getCredits(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Single<CreditsResponse>

    @GET("movie/{id}/similar")
    fun getSimilarMovies(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Single<MovieResponse>
}
