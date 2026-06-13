package com.example.topmovies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MovieRepository(app)
    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var loadedMovieId = -1

    fun load(movieId: Int) {
        if (movieId == loadedMovieId) return
        loadedMovieId = movieId

        disposables.add(
            repository.getMovieDetails(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ details ->
                    _uiState.value = DetailUiState.Success(details = details)
                    loadCredits(movieId)
                    loadSimilar(movieId)
                }, { e ->
                    _uiState.value = DetailUiState.Error(e.message ?: "Failed to load movie details")
                })
        )
    }

    private fun loadCredits(movieId: Int) {
        disposables.add(
            repository.getCredits(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ credits ->
                    (_uiState.value as? DetailUiState.Success)?.let {
                        _uiState.value = it.copy(credits = credits)
                    }
                }, {})
        )
    }

    private fun loadSimilar(movieId: Int) {
        disposables.add(
            repository.getSimilarMovies(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    (_uiState.value as? DetailUiState.Success)?.let {
                        _uiState.value = it.copy(similarMovies = response.results)
                    }
                }, {})
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
