package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizResult

class QuizActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView

    private lateinit var tvQuestion: TextView

    private lateinit var optionA: Button
    private lateinit var optionB: Button
    private lateinit var optionC: Button
    private lateinit var optionD: Button

    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    // Firebase
    private lateinit var auth: FirebaseAuth

    private var currentQuestionIndex = 0
    private var selectedAnswer = -1
    private var score = 0

    private var fileName: String? = null

    // Menyimpan jawaban user
    private val userAnswers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_kuis)

        // Firebase
        auth = FirebaseAuth.getInstance()

        initViews()
        setupData()
        loadQuestion()
        setupListeners()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)

        tvQuestion = findViewById(R.id.tvQuestion)

        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        optionA.setOnClickListener {
            selectAnswer(0)
        }

        optionB.setOnClickListener {
            selectAnswer(1)
        }

        optionC.setOnClickListener {
            selectAnswer(2)
        }

        optionD.setOnClickListener {
            selectAnswer(3)
        }

        btnPrev.setOnClickListener {

            if (currentQuestionIndex > 0) {

                currentQuestionIndex--

                loadQuestion()
            }
        }

        btnNext.setOnClickListener {

            if (selectedAnswer == -1) {

                Toast.makeText(
                    this,
                    "Pilih jawaban terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (userAnswers.size <= currentQuestionIndex) {

                userAnswers.add(selectedAnswer)

            } else {

                userAnswers[currentQuestionIndex] =
                    selectedAnswer
            }

            if (currentQuestionIndex < questions.size - 1) {

                currentQuestionIndex++
                selectedAnswer = -1

                loadQuestion()

            } else {

                calculateScore()
                saveQuizResult()
                showResult()
            }
        }
    }

    private fun loadQuestion() {

        resetButtons()

        val question =
            questions[currentQuestionIndex]

        tvQuestion.text =
            "${currentQuestionIndex + 1}. ${question.question}"

        optionA.text = question.options[0]
        optionB.text = question.options[1]
        optionC.text = question.options[2]
        optionD.text = question.options[3]

        if (currentQuestionIndex < userAnswers.size) {

            selectedAnswer =
                userAnswers[currentQuestionIndex]

            highlightSelectedButton()
        }
    }

    private fun selectAnswer(index: Int) {

        selectedAnswer = index

        resetButtons()
        highlightSelectedButton()
    }

    private fun highlightSelectedButton() {

        when (selectedAnswer) {

            0 -> optionA.setBackgroundResource(
                R.drawable.button1
            )

            1 -> optionB.setBackgroundResource(
                R.drawable.button1
            )

            2 -> optionC.setBackgroundResource(
                R.drawable.button1
            )

            3 -> optionD.setBackgroundResource(
                R.drawable.button1
            )
        }
    }

    private fun resetButtons() {

        optionA.setBackgroundResource(
            R.drawable.kontainer
        )

        optionB.setBackgroundResource(
            R.drawable.kontainer
        )

        optionC.setBackgroundResource(
            R.drawable.kontainer
        )

        optionD.setBackgroundResource(
            R.drawable.kontainer
        )
    }

    private fun calculateScore() {

        score = 0

        for (i in questions.indices) {

            if (
                userAnswers[i] ==
                questions[i].correctAnswer
            ) {

                score++
            }
        }
    }

    // ==========================
    // SIMPAN HASIL KUIS FIREBASE
    // ==========================

    private fun saveQuizResult() {

        val userId =
            auth.currentUser?.uid ?: return

        val database =
            FirebaseDatabase
                .getInstance()
                .getReference("QuizHistory")
                .child(userId)

        val quizId =
            database.push().key ?: return

        val finalScore =
            (score * 100) / questions.size

        val quizMap =
            HashMap<String, Any>()

        quizMap["fileName"] =
            fileName ?: "Unknown File"

        quizMap["score"] =
            finalScore

        quizMap["correctAnswer"] =
            score

        quizMap["wrongAnswer"] =
            questions.size - score

        quizMap["totalQuestion"] =
            questions.size

        quizMap["createdAt"] =
            System.currentTimeMillis()

        database.child(quizId)
            .setValue(quizMap)

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Hasil kuis berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal menyimpan hasil kuis",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showResult() {

        val result = QuizResult(

            totalQuestion = questions.size,

            correctAnswer = score,

            wrongAnswer = questions.size - score,

            score = (score * 100) / questions.size,

            explanations = listOf(

                "1. Jakarta adalah ibu kota Indonesia",

                "2. 2 + 5 = 7",

                "3. Jupiter merupakan planet terbesar"
            )
        )

        val intent =
            Intent(
                this,
                HasilKuisActivity::class.java
            )

        intent.putExtra(
            "QUIZ_RESULT",
            result
        )

        startActivity(intent)

        finish()
    }

    private fun setupData() {

        userAnswers.clear()

        fileName =
            intent.getStringExtra("FILE_NAME")
    }

    companion object {

        val questions = listOf(

            Question(
                question =
                    "Apa ibu kota Indonesia?",

                options = listOf(
                    "Bandung",
                    "Jakarta",
                    "Surabaya",
                    "Semarang"
                ),

                correctAnswer = 1
            ),

            Question(
                question =
                    "2 + 5 = ?",

                options = listOf(
                    "5",
                    "6",
                    "7",
                    "8"
                ),

                correctAnswer = 2
            ),

            Question(
                question =
                    "Planet terbesar di tata surya?",

                options = listOf(
                    "Mars",
                    "Venus",
                    "Jupiter",
                    "Saturnus"
                ),

                correctAnswer = 2
            )
        )
    }
}

data class Question(

    val question: String,

    val options: List<String>,

    val correctAnswer: Int
)