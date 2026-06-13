package com.example.topmovies

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        val layoutManager = LinearLayoutManager(this)

        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.gold))
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#1E1E1E"))
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (lastVisible >= adapter.itemCount - 4) viewModel.loadNextPage()
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is MovieListUiState.Loading -> {
                                adapter.setMovies(emptyList())
                                adapter.setLoading(false)
                                binding.swipeRefresh.isRefreshing = false
                            }
                            is MovieListUiState.Success -> {
                                adapter.setMovies(state.movies)
                                adapter.setLoading(state.isLoadingMore)
                                binding.swipeRefresh.isRefreshing = state.isRefreshing
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
}
