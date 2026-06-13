package com.example.topmovies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.topmovies.data.AppDatabase
import com.example.topmovies.databinding.ActivityIntroBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener { finish() }

        disposables.add(
            AppDatabase.getInstance(this).movieDao().getMoviesByPage(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movies ->
                    if (movies.isNotEmpty()) {
                        val urls = movies.map { it.posterPath.tmdbImageUrl("w342") }
                        binding.rvPosters.layoutManager = GridLayoutManager(this, 3)
                        binding.rvPosters.adapter = IntroPosterAdapter(urls)
                    }
                }, {})
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
