package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R

class QuizHistoryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView

    private lateinit var tvFileName: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTotalQuestion: TextView
    private lateinit var tvScore: TextView

    private lateinit var resultContainer: LinearLayout

    // Firebase
    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance().reference

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

        tvScore =
            findViewById(R.id.tvSkor)

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

        val uid =
            auth.currentUser?.uid

        if (uid == null) {

            Toast.makeText(
                this,
                "User belum login",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        database.child("QuizHistory")
            .child(uid)
            .orderByChild("fileName")
            .equalTo(fileName)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            tvDate.text =
                                "Belum ada history"

                            tvScore.text =
                                "0"

                            tvTotalQuestion.text =
                                "0 Soal"

                            return
                        }

                        var latestDate = "-"
                        var latestScore = 0

                        for (data in snapshot.children) {

                            latestDate =
                                data.child("date")
                                    .getValue(String::class.java)
                                    ?: "-"

                            latestScore =
                                data.child("score")
                                    .getValue(Int::class.java)
                                    ?: 0
                        }

                        tvDate.text =
                            latestDate

                        tvScore.text =
                            "$latestScore"

                        // Dummy total soal
                        tvTotalQuestion.text =
                            "3 Soal"

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

                        showQuestions(
                            questions
                        )
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Toast.makeText(
                            this@QuizHistoryActivity,
                            error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    private fun showQuestions(
        questions: List<Triple<String, String, String>>
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
                "${index + 1}. ${item.first}"

            tvCorrect.text =
                "Jawaban benar: ${item.second}"

            tvWrong.text =
                "Jawaban salah: ${item.third}"

            resultContainer.addView(view)
        }
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }
    }
}