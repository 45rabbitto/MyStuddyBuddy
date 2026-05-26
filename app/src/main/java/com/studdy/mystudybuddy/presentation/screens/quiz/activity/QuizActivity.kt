package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Intent
import android.media.MediaPlayer
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class QuizActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnMusic: ImageView

    private lateinit var tvQuestion: TextView

    private lateinit var optionA: Button
    private lateinit var optionB: Button
    private lateinit var optionC: Button
    private lateinit var optionD: Button

    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private lateinit var auth: FirebaseAuth

    private var mediaPlayer: MediaPlayer? = null
    private var isMusicOn = true

    private var currentQuestionIndex = 0
    private var selectedAnswer = -1
    private var score = 0

    private var fileName: String? = null

    private val userAnswers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_kuis)

        auth = FirebaseAuth.getInstance()

        initViews()
        setupMusic()
        setupData()
        loadQuestion()
        setupListeners()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)
        btnMusic = findViewById(R.id.btnMusic)

        tvQuestion = findViewById(R.id.tvQuestion)

        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun setupMusic() {

        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.music_quiz
        )

        mediaPlayer?.apply {

            isLooping = true
            start()
        }

        btnMusic.setImageResource(
            R.drawable.ic_music_on
        )
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {

            finish()
        }

        btnMusic.setOnClickListener {

            if (isMusicOn) {

                mediaPlayer?.pause()

                btnMusic.setImageResource(
                    R.drawable.ic_music_off
                )

            } else {

                mediaPlayer?.start()

                btnMusic.setImageResource(
                    R.drawable.ic_music_on
                )
            }

            isMusicOn = !isMusicOn
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

                userAnswers.add(
                    selectedAnswer
                )

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

        optionA.text =
            question.options[0]

        optionB.text =
            question.options[1]

        optionC.text =
            question.options[2]

        optionD.text =
            question.options[3]

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

    private fun saveQuizResult() {

        val userId =
            auth.currentUser?.uid ?: return

        val database =
            FirebaseDatabase.getInstance()
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

                // jika nilai >=70 tambahkan progress 30%
                if(finalScore >= 70){

                    updateQuizProgress(
                        fileName ?: ""
                    )
                }
            }
    }

    private fun updateQuizProgress(fileName: String) {

        val userId =
            auth.currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("Progress")
            .child(userId)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (data in snapshot.children) {

                            val savedFileName =
                                data.child("fileName")
                                    .getValue(String::class.java)

                            if (savedFileName == fileName) {

                                data.ref.child("quizDone")
                                    .setValue(true)

                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                }
            )
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

                "3. Jupiter adalah planet terbesar"
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
            intent.getStringExtra(
                "FILE_NAME"
            )
    }

    override fun onPause() {
        super.onPause()

        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()

        if (isMusicOn) {

            mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {

        val questions = listOf(

            Question(
                "Apa ibu kota Indonesia?",
                listOf(
                    "Bandung",
                    "Jakarta",
                    "Surabaya",
                    "Semarang"
                ),
                1 // Jakarta
            ),

            Question(
                "2 + 5 = ?",
                listOf(
                    "5",
                    "6",
                    "7",
                    "8"
                ),
                2 // 7
            ),

            Question(
                "Planet terbesar?",
                listOf(
                    "Mars",
                    "Venus",
                    "Jupiter",
                    "Saturnus"
                ),
                2 // Jupiter
            )
        )
    }
}

data class Question(

    val question: String,

    val options: List<String>,

    val correctAnswer: Int
)