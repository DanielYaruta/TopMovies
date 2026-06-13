package com.example.topmovies

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.topmovies.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MovieListViewModel by viewModels()
    private val adapter = MovieAdapter()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLaunchCount()
        checkApiKey()

        val layoutManager = LinearLayoutManager(this)

        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.gold))
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#1E1E1E"))
        binding.swipeRefresh.setOnRefreshListener {
            binding.etSearch.text?.clear()
            binding.etSearch.clearFocus()
            viewModel.refresh()
        }

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (lastVisible >= adapter.itemCount - 4) viewModel.loadNextPage()
            }
        })

        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchMovies(text?.toString().orEmpty())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is MovieListUiState.Loading -> {
                                adapter.setSkeletonMode()
                                binding.swipeRefresh.isRefreshing = false
                                binding.emptyState.visibility = View.GONE
                            }
                            is MovieListUiState.Success -> {
                                adapter.setMovies(state.movies)
                                adapter.setLoading(state.isLoadingMore)
                                binding.swipeRefresh.isRefreshing = state.isRefreshing
                                binding.emptyState.visibility =
                                    if (!state.isRefreshing && state.movies.isEmpty()) View.VISIBLE else View.GONE
                            }
                        }
                    }
                }
                launch {
                    viewModel.error.collect { error ->
                        if (error != null) {
                            Snackbar.make(binding.recyclerView, error, Snackbar.LENGTH_LONG)
                                .setAction("Retry") { viewModel.loadNextPage() }
                                .show()
                            viewModel.clearError()
                        }
                    }
                }
            }
        }
    }

    private fun checkApiKey() {
        if (BuildConfig.TMDB_API_KEY.isBlank()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_api_key_title))
                .setMessage(getString(R.string.error_api_key_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    private fun checkLaunchCount() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val count = prefs.getInt("launch_count", 0) + 1
        prefs.edit().putInt("launch_count", count).apply()
        if (count % 3 == 0) {
            startActivity(Intent(this, IntroActivity::class.java))
        }
    }
}
