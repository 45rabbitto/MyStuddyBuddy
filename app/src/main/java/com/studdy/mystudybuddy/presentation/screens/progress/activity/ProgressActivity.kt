package com.studdy.mystudybuddy.presentation.progress.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.data.ProgressRepository
import com.studdy.mystudybuddy.domain.ProgressManager

class ProgresActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvMateri: TextView
    private lateinit var tvKuis: TextView
    private lateinit var tvRataRata: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_progres)

        initViews()
        loadData()
        setupListeners()
    }

    private fun initViews() {

        btnBack = findViewById<ImageView>(R.id.btnBack)

        tvMateri = findViewById<TextView>(R.id.tvMateri)
        tvKuis = findViewById<TextView>(R.id.tvQuiz)
        tvRataRata = findViewById<TextView>(R.id.tvAverage)
    }

    private fun loadData() {

        tvMateri.text = "12"
        tvKuis.text = "8"
        tvRataRata.text = "87%"
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }
    }
}