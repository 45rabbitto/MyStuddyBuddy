package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.chatbot.activity.ChatbotActivity
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.HasilKuisActivity
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity

class FileHistoryDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var tvSkor: TextView
    private lateinit var btnBukaRingkasan: Button
    private lateinit var btnBukaQuiz: Button
    private lateinit var btnChatbot: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var fileName = ""
    private var fileUri = ""
    private var savedDocumentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_history)

        initViews()
        getData()
        setupListeners()
        loadDetailHistory()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvFileName = findViewById(R.id.tvFileName)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        tvSkor = findViewById(R.id.tvSkor)
        btnBukaRingkasan = findViewById(R.id.btnBukaRingkasan)
        btnBukaQuiz = findViewById(R.id.btnBukaQuiz)
        btnChatbot = findViewById(R.id.btnChatbot)
    }

    private fun getData() {
        fileName = intent.getStringExtra("FILE_NAME") ?: ""
        fileUri = intent.getStringExtra("FILE_URI") ?: ""
        savedDocumentId = intent.getStringExtra("DOCUMENT_ID")

        tvFileName.text = fileName

        Log.d("HistoryDetail", "fileName=$fileName")
        Log.d("HistoryDetail",  "Document ID = $savedDocumentId")

    }

    private fun setupListeners() {

        btnBack.setOnClickListener { finish() }

        btnBukaRingkasan.setOnClickListener {
            startActivity(
                Intent(this, RingkasanActivity::class.java).apply {
                    putExtra("FILE_NAME", fileName)
                    putExtra("FILE_URI", fileUri)
                    putExtra("DOCUMENT_ID", savedDocumentId)
                }
            )
        }

        btnBukaQuiz.setOnClickListener {

            val uid = auth.currentUser?.uid ?: return@setOnClickListener

            database.child("QuizHistory")
                .child(uid)
                .orderByChild("fileName")
                .equalTo(fileName)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (!snapshot.exists()) {
                            startActivity(
                                Intent(
                                    this@FileHistoryDetailActivity,
                                    QuizActivity::class.java
                                ).apply {
                                    putExtra("FILE_NAME", fileName)
                                    putExtra("FILE_URI", fileUri)
                                }
                            )
                            return
                        }

                        var score = 0
                        var correct = 0
                        var wrong = 0
                        var total = 0
                        val questions = arrayListOf<String>()
                        val userAnswers = arrayListOf<String>()
                        val correctAnswers = arrayListOf<String>()

                        for (data in snapshot.children) {
                            score = data.child("score").getValue(Int::class.java) ?: 0
                            correct = data.child("correctAnswer").getValue(Int::class.java) ?: 0
                            wrong = data.child("wrongAnswer").getValue(Int::class.java) ?: 0
                            total = data.child("totalQuestion").getValue(Int::class.java) ?: 0

                            data.child("questions").children.forEach {
                                it.getValue(String::class.java)?.let { q -> questions.add(q) }
                            }

                            data.child("userAnswers").children.forEach {
                                it.getValue(String::class.java)?.let { a -> userAnswers.add(a) }
                            }

                            data.child("correctAnswers").children.forEach {
                                it.getValue(String::class.java)?.let { a -> correctAnswers.add(a) }
                            }
                        }

                        startActivity(
                            Intent(
                                this@FileHistoryDetailActivity,
                                HasilKuisActivity::class.java
                            ).apply {
                                putExtra("SCORE", score)
                                putExtra("CORRECT", correct)
                                putExtra("WRONG", wrong)
                                putExtra("TOTAL", total)
                                putExtra("FILE_NAME", fileName)
                                putStringArrayListExtra("QUESTIONS", questions)
                                putStringArrayListExtra("USER_ANSWERS", userAnswers)
                                putStringArrayListExtra("CORRECT_ANSWERS", correctAnswers)
                            }
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@FileHistoryDetailActivity, error.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }

        btnChatbot.setOnClickListener {

            if (savedDocumentId.isNullOrEmpty()) {
                Toast.makeText(this, "Document ID kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(
                Intent(this, ChatbotActivity::class.java).apply {
                    putExtra("FILE_NAME", fileName)
                    putExtra("DOCUMENT_ID", savedDocumentId)
                    putExtra("FILE_URI", fileUri)
                }
            )
        }
    }

    private fun loadDetailHistory() {

        val uid = auth.currentUser?.uid ?: return


        android.util.Log.d("CHECK_HISTORY", "uid = $uid")
        android.util.Log.d("CHECK_HISTORY", "documentId = $savedDocumentId")

        database.child("QuizHistory")
            .child(uid)
            .orderByChild("fileName")
            .equalTo(fileName)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (!snapshot.exists()) {
                        tvSkor.text = "Skor : 0"
                        return
                    }

                    var score = 0

                    snapshot.children.forEach { data ->
                        score = data.child("score").getValue(Int::class.java) ?: 0
                    }

                    tvSkor.text = "Skor : $score"
                }

                override fun onCancelled(error: DatabaseError) {
                    tvSkor.text = "Gagal load skor"
                }
            })

        if (savedDocumentId.isNullOrEmpty()) {
            tvRingkasan.text = "Ringkasan belum tersedia"
            return
        }

        firestore.collection("PdfContents")
            .document(uid)
            .collection("documents")
            .document(savedDocumentId!!)
            .get()
            .addOnSuccessListener { doc ->

                Log.d("FIRESTORE_CHECK", "exists = ${doc.exists()}")
                Log.d("FIRESTORE_CHECK", "data = ${doc.data}")

                if (doc.exists()) {
                    val summary = doc.getString("summary")

                    Log.d("CHECK_HISTORY", "SUMMARY = $summary")

                    tvRingkasan.text = summary ?: "Ringkasan belum tersedia"
                } else {
                    tvRingkasan.text = "Dokumen tidak ditemukan"
                }
            }
            .addOnFailureListener {
                tvRingkasan.text = "Gagal load ringkasan"
            }
    }
}