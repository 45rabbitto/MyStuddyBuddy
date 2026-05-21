package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class FileHistoryDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var btnBukaRingkasan: Button
    private lateinit var btnBukaQuiz: Button

    private var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_file_history
        )

        initViews()
        getData()
        setupListeners()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        tvFileName =
            findViewById(R.id.tvFileName)

        btnBukaRingkasan =
            findViewById(R.id.btnBukaRingkasan)

        btnBukaQuiz =
            findViewById(R.id.btnBukaQuiz)
    }

    private fun getData() {

        fileName =
            intent.getStringExtra(
                "FILE_NAME"
            ) ?: "Dokumen"

        tvFileName.text =
            fileName
    }

    private fun setupListeners() {

        // tombol kembali
        btnBack.setOnClickListener {
            finish()
        }

        // buka history ringkasan
        btnBukaRingkasan.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SummaryHistoryActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )
                }
            )
        }

        // buka history hasil quiz
        btnBukaQuiz.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    QuizHistoryActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )

                    // sementara dummy skor
                    putExtra(
                        "SCORE",
                        85
                    )
                }
            )
        }
    }
}