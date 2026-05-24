package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizQuestionModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HasilKuisActivity : AppCompatActivity() {

    private lateinit var btnDashboard: Button
    private lateinit var tvSkor: TextView

    private val database =
        FirebaseDatabase.getInstance().reference

    private val auth =
        FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_hasil_kuis
        )

        initViews()
        setupData()
    }

    private fun initViews() {

        btnDashboard =
            findViewById(R.id.btnDashboard)

        tvSkor =
            findViewById(R.id.tvSkor)
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

        tvSkor.text =
            score.toString()

        // contoh data soal
        // nanti bisa diganti dengan data quiz asli
        val questionList =
            listOf(

                QuizQuestionModel(
                    "Apa fungsi inti sel?",
                    "Mengatur aktivitas sel",
                    "Membentuk energi"
                ),

                QuizQuestionModel(
                    "Organel penghasil energi?",
                    "Mitokondria",
                    "Mitokondria"
                ),

                QuizQuestionModel(
                    "Tempat fotosintesis?",
                    "Kloroplas",
                    "Membran Sel"
                )
            )

        saveQuizResultToFirebase(
            fileName,
            score,
            questionList
        )

        updateProgress(
            score
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

    private fun saveQuizResultToFirebase(

        fileName: String,
        score: Int,
        questions: List<QuizQuestionModel>

    ) {

        val uid =
            auth.currentUser?.uid
                ?: return

        val quizId =
            database.child(
                "QuizHistory"
            )
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

        quizData["date"] =
            currentDate

        database.child(
            "QuizHistory"
        )
            .child(uid)
            .child(quizId)
            .setValue(quizData)

        for ((index, item)
        in questions.withIndex()) {

            database.child(
                "QuizHistory"
            )
                .child(uid)
                .child(quizId)
                .child("questions")
                .child(index.toString())
                .setValue(item)
        }
    }

    private fun updateProgress(
        score: Int
    ) {

        val prefs =
            getSharedPreferences(
                "progress_data",
                MODE_PRIVATE
            )

        val quizCount =
            prefs.getInt(
                "quiz_count",
                0
            )

        val avgScore =
            prefs.getInt(
                "avg_score",
                0
            )

        val newQuizCount =
            quizCount + 1

        val newAverage =

            ((avgScore * quizCount)
                    + score) / newQuizCount


        prefs.edit()

            .putInt(
                "quiz_count",
                newQuizCount
            )

            .putInt(
                "avg_score",
                newAverage
            )

            .apply()
    }
}