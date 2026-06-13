package com.example.topmovies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.topmovies.data.Movie
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class MovieListViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MovieRepository(app)
    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 0
    private var totalPages = 1
    private var isLoading = false
    private var isSearchActive = false
    private var savedStateBeforeSearch: MovieListUiState? = null

    private val searchSubject = PublishSubject.create<String>()

    init {
        setupSearch()
        load(1)
    }

    private fun setupSearch() {
        disposables.add(
            searchSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMapSingle { query ->
                    repository.searchMovies(query)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn { emptyList() }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    _uiState.value = MovieListUiState.Success(results)
                }
        )
    }

    fun loadNextPage() {
        if (isSearchActive || isLoading || currentPage >= totalPages) return
        val movies = (_uiState.value as? MovieListUiState.Success)?.movies ?: emptyList()
        _uiState.value = if (movies.isEmpty()) MovieListUiState.Loading
                         else MovieListUiState.Success(movies, isLoadingMore = true)
        load(currentPage + 1, movies)
    }

    fun refresh() {
        if (isLoading) return
        isLoading = true
        isSearchActive = false
        savedStateBeforeSearch = null
        currentPage = 0
        totalPages = 1
        _uiState.value = MovieListUiState.Success(emptyList(), isRefreshing = true)
        disposables.add(
            repository.clearCache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    isLoading = false
                    load(1)
                }, { e ->
                    _error.value = e.message
                    isLoading = false
                })
        )
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            if (isSearchActive) {
                isSearchActive = false
                savedStateBeforeSearch?.let { _uiState.value = it }
                savedStateBeforeSearch = null
            }
            return
        }
        if (!isSearchActive) {
            isSearchActive = true
            savedStateBeforeSearch = _uiState.value
        }
        searchSubject.onNext(query)
    }

    fun clearError() { _error.value = null }

    private fun load(page: Int, existing: List<Movie> = emptyList()) {
        isLoading = true
        disposables.add(
            repository.getTopRatedMovies(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    currentPage = response.page
                    totalPages = response.totalPages
                    _uiState.value = MovieListUiState.Success(existing + response.results)
                    isLoading = false
                }, { e ->
                    _error.value = e.message ?: "Failed to load movies"
                    _uiState.value = if (existing.isEmpty()) MovieListUiState.Loading
                                     else MovieListUiState.Success(existing)
                    isLoading = false
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
