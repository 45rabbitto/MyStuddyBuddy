package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class QuizHistoryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView

    private lateinit var tvFileName: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTotalQuestion: TextView

    private lateinit var resultContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_history_quiz
        )

        initViews()
        loadData()
        setupListeners()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        tvFileName =
            findViewById(R.id.tvFileName)

        tvDate =
            findViewById(R.id.tvDate)

        tvTotalQuestion =
            findViewById(R.id.tvTotalQuestion)

        resultContainer =
            findViewById(R.id.resultContainer)
    }

    private fun loadData() {

        val fileName =
            intent.getStringExtra(
                "FILE_NAME"
            ) ?: "Dokumen"

        tvFileName.text =
            fileName

        tvDate.text =
            "20 Mei 2026"

        val questions =
            listOf(

                Triple(
                    "Apa fungsi inti sel?",
                    "Mengatur aktivitas sel",
                    "Membentuk energi"
                ),

                Triple(
                    "Organel penghasil energi adalah?",
                    "Mitokondria",
                    "Ribosom"
                ),

                Triple(
                    "Bagian tumbuhan untuk fotosintesis?",
                    "Kloroplas",
                    "Membran sel"
                )
            )

        tvTotalQuestion.text =
            "${questions.size} Soal"

        showQuestions(
            questions
        )
    }

    private fun showQuestions(
        questions: List<Triple<String,String,String>>
    ) {

        resultContainer.removeAllViews()

        for ((index, item) in questions.withIndex()) {

            val view =
                LayoutInflater.from(this)
                    .inflate(
                        R.layout.item_history_quiz,
                        resultContainer,
                        false
                    )

            val tvQuestion =
                view.findViewById<TextView>(
                    R.id.tvQuestion
                )

            val tvCorrect =
                view.findViewById<TextView>(
                    R.id.tvCorrectAnswer
                )

            val tvWrong =
                view.findViewById<TextView>(
                    R.id.tvWrongAnswer
                )

            tvQuestion.text =
                "${index+1}. ${item.first}"

            tvCorrect.text =
                item.second

            tvWrong.text =
                item.third

            resultContainer.addView(view)
        }
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }
    }
}