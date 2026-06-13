package com.example.topmovies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.topmovies.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener { finish() }
    }
}
