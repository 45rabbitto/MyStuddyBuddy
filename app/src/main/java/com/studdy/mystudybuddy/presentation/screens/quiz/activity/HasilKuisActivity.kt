package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Intent
import android.graphics.Color
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
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizQuestionModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HasilKuisActivity : AppCompatActivity() {

    private lateinit var btnDashboard: Button

    private lateinit var tvSkor: TextView
    private lateinit var tvBenar: TextView
    private lateinit var tvSalah: TextView

    private lateinit var pembahasanContainer: LinearLayout

    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_kuis)

        initViews()
        setupData()
    }

    private fun initViews() {
        btnDashboard = findViewById(R.id.btnDashboard)
        tvSkor = findViewById(R.id.tvSkor)
        tvBenar = findViewById(R.id.tvBenar)
        tvSalah = findViewById(R.id.tvSalah)
        pembahasanContainer = findViewById(R.id.pembahasanContainer)
    }

    private fun setupData() {

        val score = intent.getIntExtra("SCORE", 0)
        val fileName = intent.getStringExtra("FILE_NAME") ?: "Materi"

        // =========================
        // DATA SOAL (CONTOH)
        // =========================
        val questionList = listOf(
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

        // =========================
        // FIX BUG NEGATIF (-97)
        // =========================
        val total = questionList.size

        val correct = score.coerceIn(0, total)
        val wrong = (total - correct).coerceAtLeast(0)

        // =========================
        // SET UI
        // =========================
        tvSkor.text = correct.toString()
        tvBenar.text = correct.toString()
        tvSalah.text = wrong.toString()

        // =========================
        // PEMBAHASAN
        // =========================
        showPembahasan(questionList)

        // =========================
        // SAVE FIREBASE
        // =========================
        saveQuizResultToFirebase(fileName, correct, questionList)

        // =========================
        // BUTTON
        // =========================
        btnDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    // =========================
    // PEMBAHASAN UI
    // =========================
    private fun showPembahasan(list: List<QuizQuestionModel>) {

        pembahasanContainer.removeAllViews()

        list.forEachIndexed { index, item ->

            val text = TextView(this).apply {

                text = """
                    ${index + 1}. ${item.question}
                    ✔ Jawaban benar: ${item.correctAnswer}
                """.trimIndent()

                textSize = 15f
                setPadding(20, 20, 20, 20)
                setTextColor(Color.BLACK)

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 20)
                }
            }

            pembahasanContainer.addView(text)
        }
    }

    // =========================
    // FIREBASE SAVE
    // =========================
    private fun saveQuizResultToFirebase(
        fileName: String,
        score: Int,
        questions: List<QuizQuestionModel>
    ) {

        val uid = auth.currentUser?.uid ?: return

        val quizId = database.child("QuizHistory")
            .child(uid)
            .push()
            .key ?: return

        val currentDate = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date())

        val quizData = HashMap<String, Any>()

        quizData["fileName"] = fileName
        quizData["score"] = score
        quizData["correctAnswer"] = score
        quizData["wrongAnswer"] = questions.size - score
        quizData["totalQuestion"] = questions.size
        quizData["date"] = currentDate

        database.child("QuizHistory")
            .child(uid)
            .child(quizId)
            .setValue(quizData)

        for ((index, item) in questions.withIndex()) {
            database.child("QuizHistory")
                .child(uid)
                .child(quizId)
                .child("questions")
                .child(index.toString())
                .setValue(item)
        }
    }
}