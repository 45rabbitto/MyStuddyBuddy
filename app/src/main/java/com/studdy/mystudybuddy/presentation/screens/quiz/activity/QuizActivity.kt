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
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizQuestion
import com.studdy.mystudybuddy.presentation.screens.quiz.QuizQuestionModel
import com.studdy.mystudybuddy.presentation.screens.history.model.QuizHistoryItem

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
    private var clickSound: MediaPlayer? = null

    private var isMusicOn = true

    private var currentQuestionIndex = 0
    private var selectedAnswer = -1
    private var score = 0

    private var fileName: String? = null

    private val userAnswers = mutableListOf<Int>()

    // =========================
    // LIST SOAL
    // =========================

    private val questions =
        mutableListOf<QuizQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_kuis)

        auth = FirebaseAuth.getInstance()

        initViews()
        setupMusic()
        setupData()
        setupListeners()

        loadQuestionsFromFirebase()
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

    // =========================
    // LOAD QUESTION FIREBASE
    // =========================

    private fun loadQuestionsFromFirebase() {

        FirebaseDatabase.getInstance()
            .getReference("Questions")
            .addListenerForSingleValueEvent(

                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        questions.clear()

                        for (data in snapshot.children) {

                            val question =
                                data.getValue(
                                    QuizQuestion::class.java
                                )

                            if (question != null) {

                                questions.add(question)
                            }
                        }

                        if (questions.isNotEmpty()) {

                            loadQuestion()

                        } else {

                            Toast.makeText(
                                this@QuizActivity,
                                "Soal kosong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(
                            this@QuizActivity,
                            error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    // =========================
    // MUSIC
    // =========================

    private fun setupMusic() {

        mediaPlayer =
            MediaPlayer.create(
                this,
                R.raw.backsound
            )

        mediaPlayer?.apply {

            isLooping = true
            start()
        }

        clickSound =
            MediaPlayer.create(
                this,
                R.raw.button
            )

        btnMusic.setImageResource(
            R.drawable.ic_music_on
        )
    }

    // =========================
    // LISTENER
    // =========================

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

                selectedAnswer =
                    userAnswers[currentQuestionIndex]

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

    // =========================
    // LOAD QUESTION
    // =========================

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

    // =========================
    // SELECT ANSWER
    // =========================

    private fun selectAnswer(index: Int) {

        clickSound?.start()

        selectedAnswer = index

        resetButtons()

        highlightSelectedButton()
    }

    private fun highlightSelectedButton() {

        when (selectedAnswer) {

            0 -> optionA.setBackgroundResource(R.drawable.button1)

            1 -> optionB.setBackgroundResource(R.drawable.button1)

            2 -> optionC.setBackgroundResource(R.drawable.button1)

            3 -> optionD.setBackgroundResource(R.drawable.button1)
        }
    }

    private fun resetButtons() {

        optionA.setBackgroundResource(R.drawable.kontainer)
        optionB.setBackgroundResource(R.drawable.kontainer)
        optionC.setBackgroundResource(R.drawable.kontainer)
        optionD.setBackgroundResource(R.drawable.kontainer)
    }

    // =========================
    // SCORE
    // =========================

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

    // =========================
    // FIREBASE
    // =========================

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
    }

    // =========================
    // RESULT
    // =========================

    private fun showResult() {

        mediaPlayer?.stop()

        val pembahasanList =
            mutableListOf<QuizHistoryItem>()

        for (i in questions.indices) {

            val question =
                questions[i]

            val userAnswerText =
                question.options[userAnswers[i]]

            val correctAnswerText =
                question.options[question.correctAnswer]

            pembahasanList.add(

                QuizHistoryItem(
                    question = question.question,
                    correctAnswer = correctAnswerText,
                    userAnswer = userAnswerText
                )
            )
        }

        QuizQuestionModel.questionList =
            pembahasanList

        val intent =
            Intent(
                this,
                HasilKuisActivity::class.java
            )

        intent.putExtra("SCORE", score)

        intent.putExtra("FILE_NAME", fileName)

        startActivity(intent)

        finish()
    }

    private fun setupData() {

        userAnswers.clear()

        fileName =
            intent.getStringExtra("FILE_NAME")
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

        clickSound?.release()
        clickSound = null
    }
}