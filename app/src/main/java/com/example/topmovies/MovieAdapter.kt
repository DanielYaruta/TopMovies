package com.example.topmovies

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.topmovies.data.Movie
import com.example.topmovies.databinding.ItemLoadingBinding
import com.example.topmovies.databinding.ItemMovieBinding
import com.example.topmovies.databinding.ItemSkeletonBinding

class MovieAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val movies = mutableListOf<Movie>()
    private var showLoader = false
    private var showSkeleton = true

    companion object {
        private const val TYPE_MOVIE = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_SKELETON = 2
        private const val SKELETON_COUNT = 6
    }

    fun setSkeletonMode() {
        if (showSkeleton) return
        movies.clear()
        showSkeleton = true
        notifyDataSetChanged()
    }

    fun setMovies(newMovies: List<Movie>) {
        val wasShowingSkeleton = showSkeleton
        showSkeleton = false
        val oldMovies = movies.toList()
        movies.clear()
        movies.addAll(newMovies)

        if (wasShowingSkeleton || oldMovies.isEmpty() || newMovies.isEmpty()) {
            notifyDataSetChanged()
            return
        }

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldMovies.size
            override fun getNewListSize() = newMovies.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                oldMovies[oldPos].id == newMovies[newPos].id
            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                oldMovies[oldPos] == newMovies[newPos]
        })
        diff.dispatchUpdatesTo(this)
    }

    fun setLoading(loading: Boolean) {
        if (loading == showLoader) return
        showLoader = loading
        if (movies.isEmpty()) return
        if (loading) notifyItemInserted(movies.size)
        else notifyItemRemoved(movies.size)
    }

    override fun getItemViewType(position: Int): Int = when {
        showSkeleton -> TYPE_SKELETON
        position < movies.size -> TYPE_MOVIE
        else -> TYPE_LOADING
    }

    override fun getItemCount(): Int = when {
        showSkeleton -> SKELETON_COUNT
        else -> movies.size + if (showLoader) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_MOVIE -> MovieViewHolder(ItemMovieBinding.inflate(inflater, parent, false))
            TYPE_SKELETON -> SkeletonViewHolder(ItemSkeletonBinding.inflate(inflater, parent, false))
            else -> LoadingViewHolder(ItemLoadingBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) holder.bind(position + 1, movies[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is SkeletonViewHolder) holder.startAnimation()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is SkeletonViewHolder) holder.stopAnimation()
    }

    class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rank: Int, movie: Movie) {
            binding.tvRank.text = "#$rank"
            binding.tvTitle.text = movie.title
            binding.tvYear.text = movie.releaseDate.take(4)
            binding.tvRating.text = movie.voteAverage.toRatingText()
            binding.ivPoster.transitionName = "poster_${movie.id}"
            Glide.with(binding.root)
                .load(movie.posterPath.tmdbImageUrl())
                .placeholder(android.R.color.darker_gray)
                .into(binding.ivPoster)
            binding.root.setOnClickListener {
                val activity = binding.root.context as Activity
                val intent = Intent(activity, DetailActivity::class.java).apply {
                    putExtra("movie_id", movie.id)
                    putExtra("title", movie.title)
                    putExtra("overview", movie.overview)
                    putExtra("poster_path", movie.posterPath)
                    putExtra("backdrop_path", movie.backdropPath)
                    putExtra("vote_average", movie.voteAverage)
                    putExtra("release_date", movie.releaseDate)
                    putExtra("transition_name", "poster_${movie.id}")
                }
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, Pair(binding.ivPoster, "poster_${movie.id}")
                )
                activity.startActivity(intent, options.toBundle())
            }
        }
    }

    class SkeletonViewHolder(binding: ItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root) {
        private val animator = ObjectAnimator.ofFloat(binding.root, View.ALPHA, 1f, 0.3f).apply {
            duration = 800
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }
        fun startAnimation() = animator.start()
        fun stopAnimation() { animator.cancel(); itemView.alpha = 1f }
    }

    class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)
}
