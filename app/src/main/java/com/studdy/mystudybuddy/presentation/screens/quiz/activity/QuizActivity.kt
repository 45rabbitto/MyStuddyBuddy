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
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizQuestion

class QuizActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnMusic: ImageView

    private lateinit var tvNumber: TextView
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
    private var summaryText: String? = null
    private var jumlahSoal = 5

    private val userAnswers = mutableListOf<Int>()
    private val questions = mutableListOf<QuizQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuis)

        auth = FirebaseAuth.getInstance()

        initViews()
        setupData()
        setupMusic()
        setupListeners()

        generateDummyQuestions()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)
        btnMusic = findViewById(R.id.btnMusic)

        tvNumber = findViewById(R.id.tvNumber)
        tvQuestion = findViewById(R.id.tvQuestion)

        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun setupData() {

        fileName = intent.getStringExtra("FILE_NAME")
        summaryText = intent.getStringExtra("RINGKASAN")
        jumlahSoal = intent.getIntExtra("JUMLAH_SOAL", 5)

        userAnswers.clear()
    }

    private fun setupMusic() {

        mediaPlayer =
            MediaPlayer.create(this, R.raw.backsound)

        mediaPlayer?.apply {
            isLooping = true
            start()
        }

        clickSound =
            MediaPlayer.create(this, R.raw.button)

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

                selectedAnswer =
                    if (currentQuestionIndex < userAnswers.size)
                        userAnswers[currentQuestionIndex]
                    else
                        -1

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

                selectedAnswer =
                    if (currentQuestionIndex < userAnswers.size)
                        userAnswers[currentQuestionIndex]
                    else
                        -1

                loadQuestion()

            } else {

                // simpan jawaban soal terakhir
                if (userAnswers.size <= currentQuestionIndex) {
                    userAnswers.add(selectedAnswer)
                } else {
                    userAnswers[currentQuestionIndex] = selectedAnswer
                }

                calculateScore()

                saveQuizResult()

                showResult()
            }
        }
    }

    private fun generateDummyQuestions() {

        val allQuestions = mutableListOf(

            QuizQuestion(
                "Apa fungsi utama sistem operasi?",
                listOf(
                    "Mengelola hardware dan software",
                    "Membuat dokumen",
                    "Menyimpan data",
                    "Membuka browser"
                ),
                0
            ),

            QuizQuestion(
                "Bahasa resmi Android saat ini adalah?",
                listOf(
                    "Java",
                    "PHP",
                    "Kotlin",
                    "Swift"
                ),
                2
            ),

            QuizQuestion(
                "Apa kepanjangan CPU?",
                listOf(
                    "Central Process Unit",
                    "Computer Processing Unit",
                    "Central Processing Unit",
                    "Central Program Unit"
                ),
                2
            ),

            QuizQuestion(
                "Git digunakan untuk?",
                listOf(
                    "Database",
                    "Version Control",
                    "UI Design",
                    "Hosting"
                ),
                1
            ),

            QuizQuestion(
                "Perintah SQL mengambil data?",
                listOf(
                    "INSERT",
                    "DELETE",
                    "UPDATE",
                    "SELECT"
                ),
                3
            ),

            QuizQuestion(
                "FIFO digunakan pada?",
                listOf(
                    "Stack",
                    "Queue",
                    "Tree",
                    "Graph"
                ),
                1
            )
        )

        allQuestions.shuffle()

        questions.clear()

        questions.addAll(
            allQuestions.take(jumlahSoal)
        )

        loadQuestion()
    }

    private fun loadQuestion() {

        resetButtons()

        val question =
            questions[currentQuestionIndex]

        tvNumber.text =
            "Soal ${currentQuestionIndex + 1} dari ${questions.size}"

        tvQuestion.text =
            question.question

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

    private fun calculateScore() {

        score = 0

        for (i in questions.indices) {

            if (
                i < userAnswers.size &&
                userAnswers[i] == questions[i].correctAnswer
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

        val questionList = ArrayList<String>()
        val userAnswerList = ArrayList<String>()
        val correctAnswerList = ArrayList<String>()

        for (i in questions.indices) {

            questionList.add(
                questions[i].question
            )

            userAnswerList.add(
                questions[i].options[userAnswers[i]]
            )

            correctAnswerList.add(
                questions[i].options[
                    questions[i].correctAnswer
                ]
            )
        }

        val finalScore =
            (score * 100) / questions.size

        val quizData = hashMapOf<String, Any>(

            "fileName" to (fileName ?: "Materi"),

            "score" to finalScore,

            "correctAnswer" to score,

            "wrongAnswer" to (questions.size - score),

            "totalQuestion" to questions.size,

            "questions" to questionList,

            "userAnswers" to userAnswerList,

            "correctAnswers" to correctAnswerList,

            "createdAt" to System.currentTimeMillis()
        )

        database.child(quizId)
            .setValue(quizData)
    }

    private fun showResult() {

        val correct = score
        val wrong = questions.size - score

        val questionList = ArrayList<String>()
        val userAnswerList = ArrayList<String>()
        val correctAnswerList = ArrayList<String>()

        for (i in questions.indices) {

            questionList.add(questions[i].question)

            userAnswerList.add(
                questions[i].options[userAnswers[i]]
            )

            correctAnswerList.add(
                questions[i].options[
                    questions[i].correctAnswer
                ]
            )
        }

        startActivity(
            Intent(this, HasilKuisActivity::class.java).apply {

                putExtra("CORRECT", score)
                putExtra("WRONG", questions.size - score)
                putExtra("SCORE", (score * 100) / questions.size)

                putExtra("TOTAL_QUESTIONS", questions.size)

                putExtra("FILE_NAME", fileName)

                putStringArrayListExtra(
                    "QUESTIONS",
                    questionList
                )

                putStringArrayListExtra(
                    "USER_ANSWERS",
                    userAnswerList
                )

                putStringArrayListExtra(
                    "CORRECT_ANSWERS",
                    correctAnswerList
                )
            }
        )

        finish()
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
