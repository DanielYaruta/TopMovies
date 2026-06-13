package com.example.topmovies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.topmovies.data.Movie
import com.example.topmovies.databinding.ItemSimilarMovieBinding

class SimilarMovieAdapter(private val movies: List<Movie>) :
    RecyclerView.Adapter<SimilarMovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemSimilarMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(movies[position])

    override fun getItemCount() = movies.size

    class ViewHolder(private val binding: ItemSimilarMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.tvSimilarTitle.text = movie.title
            binding.tvSimilarRating.text = movie.voteAverage.toRatingText()
            Glide.with(binding.root)
                .load(movie.posterPath.tmdbImageUrl())
                .placeholder(android.R.color.darker_gray)
                .into(binding.ivSimilarPoster)
        }
    }
}
