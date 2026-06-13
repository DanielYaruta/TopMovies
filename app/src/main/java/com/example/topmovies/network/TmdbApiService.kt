package com.example.topmovies.network

import com.example.topmovies.data.CreditsResponse
import com.example.topmovies.data.MovieDetails
import com.example.topmovies.data.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDetails

    @GET("movie/{id}/credits")
    suspend fun getCredits(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String
    ): CreditsResponse

    @GET("movie/{id}/similar")
    suspend fun getSimilarMovies(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse
}
