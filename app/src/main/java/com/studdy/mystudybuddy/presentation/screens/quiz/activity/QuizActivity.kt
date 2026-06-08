package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizQuestion

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
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth
    private var mediaPlayer: MediaPlayer? = null
    private var clickSound: MediaPlayer? = null
    private var isMusicOn = true

    private var currentQuestionIndex = 0
    private var selectedAnswer = -1
    private var score = 0

    private var fileName: String? = null
    private var summaryText: String? = null
    private var jumlahSoal: Int = 5

    private val userAnswers = mutableListOf<Int>()
    private val questions = mutableListOf<QuizQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuis)
        auth = FirebaseAuth.getInstance()
        initViews()
        setupMusic()
        setupData()
        setupListeners()
        generateDummyQuestions()
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
        progressBar = findViewById(R.id.progressBar)
        setQuizVisible(false)
    }

    private fun setQuizVisible(visible: Boolean) {
        val v = if (visible) View.VISIBLE else View.GONE
        tvQuestion.visibility = v
        optionA.visibility = v
        optionB.visibility = v
        optionC.visibility = v
        optionD.visibility = v
        btnPrev.visibility = v
        btnNext.visibility = v
        progressBar.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun setupData() {
        userAnswers.clear()
        fileName = intent.getStringExtra("FILE_NAME")
        summaryText = intent.getStringExtra("RINGKASAN")
        jumlahSoal = intent.getIntExtra("JUMLAH_SOAL", 5)
    }

    // =========================
    // DUMMY QUESTIONS
    // Nanti ganti fungsi ini saja dengan pemanggilan AI
    // =========================
    private fun generateDummyQuestions() {

        // Simulasi loading sebentar agar UX terasa natural
        progressBar.visibility = View.VISIBLE

        val allDummyQuestions = mutableListOf(
            QuizQuestion(
                question = "Apa fungsi utama dari sistem operasi?",
                options = listOf(
                    "Mengelola hardware dan software",
                    "Membuat dokumen teks",
                    "Menjalankan browser internet",
                    "Menyimpan file secara permanen"
                ),
                correctAnswer = 0
            ),
            QuizQuestion(
                question = "Struktur data yang menggunakan prinsip FIFO adalah?",
                options = listOf(
                    "Stack",
                    "Queue",
                    "Tree",
                    "Graph"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Bahasa pemrograman yang digunakan untuk membuat aplikasi Android adalah?",
                options = listOf(
                    "Swift",
                    "Python",
                    "Kotlin",
                    "PHP"
                ),
                correctAnswer = 2
            ),
            QuizQuestion(
                question = "Apa kepanjangan dari CPU?",
                options = listOf(
                    "Central Process Unit",
                    "Computer Personal Unit",
                    "Central Personal Utility",
                    "Central Processing Unit"
                ),
                correctAnswer = 3
            ),
            QuizQuestion(
                question = "Protokol yang digunakan untuk mengakses halaman web adalah?",
                options = listOf(
                    "FTP",
                    "HTTP",
                    "SMTP",
                    "SSH"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Apa yang dimaksud dengan RAM?",
                options = listOf(
                    "Penyimpanan permanen data",
                    "Memori sementara yang digunakan saat program berjalan",
                    "Prosesor utama komputer",
                    "Kartu grafis untuk rendering"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Algoritma pengurutan yang memiliki kompleksitas O(n log n) adalah?",
                options = listOf(
                    "Bubble Sort",
                    "Selection Sort",
                    "Merge Sort",
                    "Insertion Sort"
                ),
                correctAnswer = 2
            ),
            QuizQuestion(
                question = "Dalam basis data relasional, perintah untuk mengambil data adalah?",
                options = listOf(
                    "INSERT",
                    "UPDATE",
                    "DELETE",
                    "SELECT"
                ),
                correctAnswer = 3
            ),
            QuizQuestion(
                question = "Apa itu polymorphism dalam pemrograman berorientasi objek?",
                options = listOf(
                    "Kemampuan objek untuk menyembunyikan data",
                    "Kemampuan satu fungsi berperilaku berbeda tergantung konteks",
                    "Proses mewarisi sifat dari class lain",
                    "Membuat objek dari sebuah class"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Apa fungsi dari Git dalam pengembangan software?",
                options = listOf(
                    "Menjalankan server database",
                    "Mendesain antarmuka pengguna",
                    "Version control untuk mengelola perubahan kode",
                    "Mengcompile kode program"
                ),
                correctAnswer = 2
            ),
            QuizQuestion(
                question = "Singkatan API dalam pengembangan software adalah?",
                options = listOf(
                    "Application Programming Interface",
                    "Automatic Process Integration",
                    "Application Process Input",
                    "Automated Programming Input"
                ),
                correctAnswer = 0
            ),
            QuizQuestion(
                question = "Tipe data yang hanya menyimpan nilai true atau false disebut?",
                options = listOf(
                    "Integer",
                    "String",
                    "Boolean",
                    "Float"
                ),
                correctAnswer = 2
            ),
            QuizQuestion(
                question = "Apa itu inheritance dalam OOP?",
                options = listOf(
                    "Menyembunyikan detail implementasi",
                    "Membungkus data dan fungsi dalam satu unit",
                    "Mewariskan sifat dan perilaku dari class induk ke class anak",
                    "Membuat banyak objek dari satu class"
                ),
                correctAnswer = 2
            ),
            QuizQuestion(
                question = "Format file yang umum digunakan untuk pertukaran data antar aplikasi adalah?",
                options = listOf(
                    "PDF",
                    "JSON",
                    "PNG",
                    "MP4"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Apa fungsi dari firewall dalam jaringan komputer?",
                options = listOf(
                    "Mempercepat koneksi internet",
                    "Menyimpan data di cloud",
                    "Memfilter lalu lintas jaringan berdasarkan aturan keamanan",
                    "Mengenkripsi seluruh data di hard disk"
                ),
                correctAnswer = 2
            ),
            QuizQuestion(
                question = "Dalam Android, komponen yang digunakan untuk menampilkan satu layar UI disebut?",
                options = listOf(
                    "Service",
                    "Activity",
                    "BroadcastReceiver",
                    "ContentProvider"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Apa itu encapsulation dalam OOP?",
                options = listOf(
                    "Membuat objek dari class",
                    "Membungkus data dan method dalam satu unit serta menyembunyikan detail implementasi",
                    "Mewariskan properti ke class turunan",
                    "Menggunakan satu nama fungsi untuk banyak implementasi"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Struktur data yang berbentuk hierarki seperti pohon disebut?",
                options = listOf(
                    "Array",
                    "Queue",
                    "Stack",
                    "Tree"
                ),
                correctAnswer = 3
            ),
            QuizQuestion(
                question = "Apa itu cloud computing?",
                options = listOf(
                    "Komputer yang dipasang di langit-langit ruangan",
                    "Layanan komputasi melalui internet tanpa infrastruktur lokal",
                    "Software untuk menggambar desain grafis",
                    "Jaringan komputer dalam satu gedung"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "Perintah Git untuk menyimpan perubahan ke repository adalah?",
                options = listOf(
                    "git push",
                    "git pull",
                    "git commit",
                    "git merge"
                ),
                correctAnswer = 2
            )
        )

        // Acak urutan soal lalu ambil sejumlah jumlahSoal
        allDummyQuestions.shuffle()
        questions.clear()
        questions.addAll(allDummyQuestions.take(jumlahSoal))

        setQuizVisible(true)
        loadQuestion()
    }

    private fun setupMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.backsound)
        mediaPlayer?.apply {
            isLooping = true
            start()
        }
        clickSound = MediaPlayer.create(this, R.raw.button)
        btnMusic.setImageResource(R.drawable.ic_music_on)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnMusic.setOnClickListener {
            if (isMusicOn) {
                mediaPlayer?.pause()
                btnMusic.setImageResource(R.drawable.ic_music_off)
            } else {
                mediaPlayer?.start()
                btnMusic.setImageResource(R.drawable.ic_music_on)
            }
            isMusicOn = !isMusicOn
        }

        optionA.setOnClickListener { selectAnswer(0) }
        optionB.setOnClickListener { selectAnswer(1) }
        optionC.setOnClickListener { selectAnswer(2) }
        optionD.setOnClickListener { selectAnswer(3) }

        btnPrev.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                selectedAnswer = if (currentQuestionIndex < userAnswers.size)
                    userAnswers[currentQuestionIndex] else -1
                loadQuestion()
            }
        }

        btnNext.setOnClickListener {
            if (selectedAnswer == -1) {
                Toast.makeText(this, "Pilih jawaban terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userAnswers.size <= currentQuestionIndex) {
                userAnswers.add(selectedAnswer)
            } else {
                userAnswers[currentQuestionIndex] = selectedAnswer
            }

            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                selectedAnswer = if (currentQuestionIndex < userAnswers.size)
                    userAnswers[currentQuestionIndex] else -1
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
        val question = questions[currentQuestionIndex]
        tvQuestion.text = "${currentQuestionIndex + 1}. ${question.question}"
        optionA.text = question.options[0]
        optionB.text = question.options[1]
        optionC.text = question.options[2]
        optionD.text = question.options[3]

        if (currentQuestionIndex < userAnswers.size) {
            selectedAnswer = userAnswers[currentQuestionIndex]
            highlightSelectedButton()
        } else {
            selectedAnswer = -1
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
            if (i < userAnswers.size && userAnswers[i] == questions[i].correctAnswer) {
                score++
            }
        }
    }

    private fun saveQuizResult() {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
            .getReference("QuizHistory").child(userId)
        val quizId = database.push().key ?: return
        val finalScore = (score * 100) / questions.size

        database.child(quizId).setValue(
            hashMapOf<String, Any>(
                "fileName" to (fileName ?: "Unknown"),
                "score" to finalScore,
                "correctAnswer" to score,
                "wrongAnswer" to (questions.size - score),
                "totalQuestion" to questions.size,
                "createdAt" to System.currentTimeMillis()
            )
        )
    }

    private fun showResult() {
        mediaPlayer?.stop()

        val questionList = ArrayList<String>()
        val userAnswerList = ArrayList<String>()
        val correctAnswerList = ArrayList<String>()

        for (i in questions.indices) {
            questionList.add(questions[i].question)
            userAnswerList.add(questions[i].options[userAnswers[i]])
            correctAnswerList.add(questions[i].options[questions[i].correctAnswer])
        }

        startActivity(
            Intent(this, HasilKuisActivity::class.java).apply {
                putExtra("SCORE", score)
                putExtra("FILE_NAME", fileName)
                putExtra("TOTAL_QUESTIONS", questions.size)
                putStringArrayListExtra("QUESTIONS", questionList)
                putStringArrayListExtra("USER_ANSWERS", userAnswerList)
                putStringArrayListExtra("CORRECT_ANSWERS", correctAnswerList)
            }
        )
        finish()
    }

    override fun onPause() { super.onPause(); mediaPlayer?.pause() }
    override fun onResume() { super.onResume(); if (isMusicOn) mediaPlayer?.start() }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release(); mediaPlayer = null
        clickSound?.release(); clickSound = null
    }
}