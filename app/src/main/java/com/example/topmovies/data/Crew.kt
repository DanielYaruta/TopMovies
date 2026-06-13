package com.example.topmovies.data

import com.google.gson.annotations.SerializedName

data class Crew(
    val id: Int,
    val name: String,
    val job: String,
    val department: String,
    @SerializedName("profile_path") val profilePath: String?
)
