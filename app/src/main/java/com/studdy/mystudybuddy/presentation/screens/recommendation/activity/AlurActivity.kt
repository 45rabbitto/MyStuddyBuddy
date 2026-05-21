package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.presentation.screens.recommendation.adaptor.AlurAdapter
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity

class AlurActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_alur
        )

        initViews()

        setupListeners()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        imgLogo =
            findViewById(R.id.imgLogo)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {

            finish()
        }

        imgLogo.setOnClickListener {

            Toast.makeText(
                this,
                "Alur belajar aktif",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Untuk membuka latihan/quiz
        findViewById<android.widget.TextView>(
            R.id.tvMulaiLatihan
        )?.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    QuizActivity::class.java
                )
            )
        }

        // Untuk membuka detail rekomendasi
        findViewById<android.widget.TextView>(
            R.id.tvRekomendasi
        )?.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RingkasanActivity::class.java
                )
            )
        }
    }
}