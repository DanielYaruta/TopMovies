package com.example.topmovies

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.topmovies.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private val viewModel: DetailViewModel by viewModels()
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransitions()
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra("movie_id", -1)
        val posterPath = intent.getStringExtra("poster_path")
        val backdropPath = intent.getStringExtra("backdrop_path")
        val voteAverage = intent.getDoubleExtra("vote_average", 0.0)
        val releaseDate = intent.getStringExtra("release_date") ?: ""

        binding.ivPoster.transitionName = intent.getStringExtra("transition_name")
        binding.tvTitle.text = intent.getStringExtra("title") ?: ""
        binding.tvOverview.text = intent.getStringExtra("overview") ?: ""
        binding.tvRating.text = voteAverage.toRatingText()
        binding.tvYear.text = releaseDate.take(4)

        Glide.with(this)
            .load(backdropPath.tmdbImageUrl("w780"))
            .placeholder(android.R.color.darker_gray)
            .into(binding.ivBackdrop)

        postponeEnterTransition()
        Glide.with(this)
            .load(posterPath.tmdbImageUrl("w342"))
            .placeholder(android.R.color.darker_gray)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    startPostponedEnterTransition(); return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    startPostponedEnterTransition(); return false
                }
            })
            .into(binding.ivPoster)

        binding.rvCast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSimilar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        if (movieId != -1) viewModel.load(movieId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is DetailUiState.Loading -> Unit
                            is DetailUiState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            }
                            is DetailUiState.Success -> {
                                state.details?.let { details ->
                                    val runtime = details.runtime
                                    if (runtime != null && runtime > 0) {
                                        val formatted = if (runtime >= 60) "${runtime / 60}h ${runtime % 60}m" else "${runtime}m"
                                        binding.tvRuntime.text = "· $formatted"
                                    }
                                    if (details.genres.isNotEmpty()) {
                                        binding.tvGenres.text = details.genres.joinToString(" · ") { it.name }
                                    }
                                }
                                state.credits?.let { credits ->
                                    val director = credits.crew.firstOrNull { it.job == "Director" }
                                    if (director != null) {
                                        binding.tvDirector.text = "Director: ${director.name}"
                                    }
                                    val topCast = credits.cast.sortedBy { it.order }.take(10)
                                    binding.rvCast.adapter = CastAdapter(topCast)
                                }
                                if (state.similarMovies.isNotEmpty()) {
                                    binding.tvSimilarLabel.visibility = View.VISIBLE
                                    binding.rvSimilar.visibility = View.VISIBLE
                                    binding.rvSimilar.adapter = SimilarMovieAdapter(state.similarMovies)
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener { finishAfterTransition() }
    }

    private fun setupTransitions() {
        with(window) {
            sharedElementEnterTransition = TransitionSet().apply {
                addTransition(ChangeBounds())
                addTransition(ChangeImageTransform())
                duration = 350
            }
            enterTransition = Fade().apply { duration = 250 }
            returnTransition = Fade().apply { duration = 200 }
        }
    }
}
