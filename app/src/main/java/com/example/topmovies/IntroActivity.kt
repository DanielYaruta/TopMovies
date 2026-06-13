package com.example.topmovies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.topmovies.data.AppDatabase
import com.example.topmovies.databinding.ActivityIntroBinding
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener { finish() }

        lifecycleScope.launch {
            val movies = AppDatabase.getInstance(this@IntroActivity).movieDao().getMoviesByPage(1)
            if (movies.isNotEmpty()) {
                val urls = movies.map { "https://image.tmdb.org/t/p/w342${it.posterPath}" }
                binding.rvPosters.layoutManager = GridLayoutManager(this@IntroActivity, 3)
                binding.rvPosters.adapter = IntroPosterAdapter(urls)
            }
        }
    }
}
