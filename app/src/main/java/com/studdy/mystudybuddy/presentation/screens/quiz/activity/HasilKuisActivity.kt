package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HasilKuisActivity : AppCompatActivity() {

    private lateinit var btnDashboard: Button

    private lateinit var tvSkor: TextView
    private lateinit var tvBenar: TextView
    private lateinit var tvSalah: TextView

    private lateinit var pembahasanContainer: LinearLayout

    private val database =
        FirebaseDatabase.getInstance().reference

    private val auth =
        FirebaseAuth.getInstance()

    private var finishSound: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_hasil_kuis)

        finishSound =
            MediaPlayer.create(
                this,
                R.raw.finish
            )

        finishSound?.start()

        initViews()
        setupData()
    }

    private fun initViews() {

        btnDashboard =
            findViewById(R.id.btnDashboard)

        tvSkor =
            findViewById(R.id.tvSkor)

        tvBenar =
            findViewById(R.id.tvBenar)

        tvSalah =
            findViewById(R.id.tvSalah)

        pembahasanContainer =
            findViewById(R.id.pembahasanContainer)
    }
    private fun setupData() {

        val score =
            intent.getIntExtra(
                "SCORE",
                0
            )

        val fileName =
            intent.getStringExtra(
                "FILE_NAME"
            ) ?: "Materi"

        val questions =
            intent.getStringArrayListExtra(
                "QUESTIONS"
            ) ?: arrayListOf()

        val userAnswers =
            intent.getStringArrayListExtra(
                "USER_ANSWERS"
            ) ?: arrayListOf()


        val correctAnswers =
            intent.getStringArrayListExtra("CORRECT_ANSWERS")
                ?: arrayListOf()

        showPembahasan(
            questions,
            userAnswers,
            correctAnswers
        )

        val total =
            questions.size

        val correct =
            intent.getIntExtra("CORRECT", 0)

        val wrong =
            intent.getIntExtra("WRONG", 0)

        tvSkor.text =
            "$score"

        tvBenar.text =
            "$correct"

        tvSalah.text =
            "$wrong"

        showPembahasan(
            questions,
            userAnswers,
            correctAnswers
        )

        btnDashboard.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    DashboardActivity::class.java
                )
            )

            finish()
        }
    }
    private fun showPembahasan(
        questions: ArrayList<String>,
        userAnswers: ArrayList<String>,
        correctAnswers: ArrayList<String>
    ) {

        pembahasanContainer.removeAllViews()

        for (i in questions.indices) {

            val status =
                if (
                    userAnswers[i] ==
                    correctAnswers[i]
                ) {
                    "BENAR"
                } else {
                    "SALAH"
                }

            val text =
                TextView(this).apply {

                    text =
                        """
${i + 1}. ${questions[i]}

Jawaban Kamu:
${userAnswers[i]}

Jawaban Benar:
${correctAnswers[i]}

Status:
$status
                        """.trimIndent()

                    textSize = 15f

                    setPadding(
                        20,
                        20,
                        20,
                        20
                    )

                    setTextColor(Color.BLACK)

                    gravity = Gravity.START

                    setBackgroundResource(
                        R.drawable.kontainer
                    )

                    layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {

                            setMargins(
                                0,
                                0,
                                0,
                                20
                            )
                        }
                }

            pembahasanContainer.addView(text)
        }
    }

    private fun saveQuizResultToFirebase(
        fileName: String,
        score: Int,
        totalQuestion: Int
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        val quizId =
            database.child("QuizHistory")
                .child(uid)
                .push()
                .key ?: return

        val currentDate =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(Date())

        val quizData =
            HashMap<String, Any>()

        quizData["fileName"] =
            fileName

        quizData["score"] =
            score

        quizData["correctAnswer"] =
            score

        quizData["wrongAnswer"] =
            totalQuestion - score

        quizData["totalQuestion"] =
            totalQuestion

        quizData["date"] =
            currentDate

        database.child("QuizHistory")
            .child(uid)
            .child(quizId)
            .setValue(quizData)
    }

    override fun onDestroy() {
        super.onDestroy()

        finishSound?.release()
        finishSound = null
    }
}