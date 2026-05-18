package com.studdy.mystudybuddy.presentation.main

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class MainActivity : AppCompatActivity() {

    private lateinit var history: ImageView
    private lateinit var alur: ImageView
    private lateinit var progres: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        history = findViewById(R.id.history)
        alur = findViewById(R.id.alur)
        progres = findViewById(R.id.progres)
    }

    private fun setupClickListeners() {

        history.setOnClickListener {
            // buka HistoryActivity
        }

        alur.setOnClickListener {
            // buka AlurBelajarActivity
        }

        progres.setOnClickListener {
            // buka ProgressActivity
        }
    }
}