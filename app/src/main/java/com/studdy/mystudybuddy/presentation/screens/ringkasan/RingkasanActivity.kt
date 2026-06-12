package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.utils.LoggingHelper

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnFinishRingkasan: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    private var fileUri: String? = null
    private var currentSummaryText: String = ""
    private var currentDocumentId: String? = null
    private var currentFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)
        auth = FirebaseAuth.getInstance()
        initViews()
        setupClickListeners()
        loadPdfData()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnFinishRingkasan = findViewById(R.id.btnFinishRingkasan)
        progressBar = findViewById(R.id.progressBarRingkasan)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnFinishRingkasan.setOnClickListener { showFeedbackDialog() }

        btnGenerate.setOnClickListener {
            if (currentSummaryText.isEmpty()) {
                Toast.makeText(this, "Ringkasan belum tersedia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showJumlahSoalDialog()
        }
    }

    private fun loadPdfData() {
        currentDocumentId = intent.getStringExtra("DOCUMENT_ID")
        currentFileName = intent.getStringExtra("FILE_NAME")
        val pdfText = intent.getStringExtra("PDF_TEXT")

        if (!pdfText.isNullOrEmpty()) {
            generateRingkasanFromAI(pdfText, currentFileName ?: "File PDF")
            return
        }

        if (currentDocumentId != null) {
            loadPdfTextFromFirestore(currentDocumentId!!)
        } else {
            tvRingkasan.text = "Teks PDF tidak ditemukan"
        }
    }

    private fun loadPdfTextFromFirestore(documentId: String) {
        setLoading(true)
        firestore.collection("PdfContents")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val content = document.getString("content") ?: ""
                    val fileName = document.getString("fileName") ?: "File PDF"
                    currentFileName = fileName
                    generateRingkasanFromAI(content, fileName)
                } else {
                    setLoading(false)
                    tvRingkasan.text = "Dokumen tidak ditemukan"
                }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal memuat: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun generateRingkasanFromAI(pdfText: String, fileName: String) {

        val originalLength = pdfText.length

        setLoading(true)
        tvRingkasan.text = "Sedang membuat ringkasan..."

        OpenAiHelper.generateRingkasan(
            pdfText = pdfText,

            onSuccess = { ringkasan ->
                runOnUiThread {

                    setLoading(false)

                    currentSummaryText = ringkasan

                    tvRingkasan.text = "📄 $fileName\n\n$ringkasan"

                    saveSummaryToFirestore(
                        currentDocumentId,
                        ringkasan,
                        fileName,
                        originalLength
                    )
                }
            },

            onError = { error ->
                runOnUiThread {

                    setLoading(false)

                    currentSummaryText = pdfText.take(3000)

                    tvRingkasan.text =
                        "📄 $fileName\n\n${pdfText.take(3000)}"

                    Toast.makeText(
                        this,
                        "AI gagal, menampilkan teks asli: $error",
                        Toast.LENGTH_LONG
                    ).show()

                    saveSummaryToFirestore(
                        currentDocumentId,
                        currentSummaryText,
                        fileName,
                        originalLength
                    )
                }
            }
        )
    }

    private fun saveSummaryToFirestore(
        documentId: String?,
        summaryText: String,
        fileName: String,
        originalLength: Int
    ) {

        if (documentId == null) {
            android.util.Log.w(
                "RingkasanActivity",
                "Document ID null"
            )
            return
        }

        val userId = auth.currentUser?.uid ?: "guest"

        val summaryData = hashMapOf<String, Any>(
            "summary" to summaryText,
            "summaryLength" to summaryText.length,
            "originalLength" to originalLength,
            "summaryTimestamp" to System.currentTimeMillis(),
            "userId" to userId
        )

        firestore.collection("PdfContents")
            .document(documentId)
            .update(summaryData)

            .addOnSuccessListener {

                android.util.Log.d(
                    "FIRESTORE",
                    "Summary saved for doc $documentId"
                )

                android.util.Log.d(
                    "TEST",
                    "SEBELUM LoggingHelper"
                )

                LoggingHelper.logTextLength(
                    documentId = documentId,
                    type = "summaries",
                    text = summaryText,
                    originalLength = originalLength,
                    userId = userId,
                    fileName = fileName
                )

                android.util.Log.d(
                    "TEST",
                    "SESUDAH LoggingHelper"
                )
            }

            .addOnFailureListener { e ->

                android.util.Log.e(
                    "FIRESTORE",
                    "Failed to save summary: ${e.message}"
                )

                Toast.makeText(
                    this,
                    "Gagal menyimpan ringkasan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnGenerate.isEnabled = !isLoading
        btnFinishRingkasan.isEnabled = !isLoading
    }

    private fun showJumlahSoalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_generate_kuis, null)

        val tvJumlahSoal = dialogView.findViewById<TextView>(R.id.tvJumlahSoal)
        val btnMinus = dialogView.findViewById<Button>(R.id.btnMinus)
        val btnPlus = dialogView.findViewById<Button>(R.id.btnPlus)
        val btnStartQuiz = dialogView.findViewById<Button>(R.id.btnStartQuiz)

        var jumlahSoal = 5
        tvJumlahSoal.text = jumlahSoal.toString()

        btnMinus.setOnClickListener {
            if (jumlahSoal > 3) {
                jumlahSoal--
                tvJumlahSoal.text = jumlahSoal.toString()
            } else {
                Toast.makeText(this, "Minimal 3 soal", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlus.setOnClickListener {
            if (jumlahSoal < 20) {
                jumlahSoal++
                tvJumlahSoal.text = jumlahSoal.toString()
            } else {
                Toast.makeText(this, "Maksimal 20 soal", Toast.LENGTH_SHORT).show()
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnStartQuiz.setOnClickListener {
            dialog.dismiss()
            saveSummaryToFirebase(currentSummaryText)

            val namaFile = this.intent.getStringExtra("FILE_NAME")
            val quizIntent = Intent(this, QuizActivity::class.java).apply {
                putExtra("RINGKASAN", currentSummaryText)
                putExtra("FILE_URI", fileUri)
                putExtra("FILE_NAME", namaFile)
                putExtra("JUMLAH_SOAL", jumlahSoal)
            }
            startActivity(quizIntent)
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showFeedbackDialog() {
        val view = layoutInflater.inflate(R.layout.avtivity_feedback, null)
        val ivLike = view.findViewById<ImageView>(R.id.ivLike)
        val ivDislike = view.findViewById<ImageView>(R.id.ivDislike)
        val dialog = AlertDialog.Builder(this)
            .setView(view).setCancelable(false).create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ivLike.setOnClickListener {
            saveFeedback("LIKE")
            saveProgressMateri()
            Toast.makeText(this, "Terima kasih 😊", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        ivDislike.setOnClickListener {
            saveFeedback("DISLIKE")
            saveProgressMateri()
            Toast.makeText(this, "Feedback tersimpan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun saveSummaryToFirebase(summaryText: String) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("Summaries").child(userId)
        val summaryId = database.push().key ?: return
        database.child(summaryId).setValue(
            hashMapOf<String, Any>(
                "summaryText" to summaryText,
                "createdAt" to System.currentTimeMillis()
            )
        )
    }

    private fun saveFeedback(feedback: String) {
        val userId = auth.currentUser?.uid ?: return
        val fileName = intent.getStringExtra("FILE_NAME") ?: "Materi"
        val database = FirebaseDatabase.getInstance().getReference("Feedback").child(userId)
        val feedbackId = database.push().key ?: return
        database.child(feedbackId).setValue(
            hashMapOf<String, Any>(
                "feedback" to feedback,
                "fileName" to fileName,
                "createdAt" to System.currentTimeMillis()
            )
        )
    }

    private fun saveProgressMateri() {
        val userId = auth.currentUser?.uid ?: return
        val fileName = intent.getStringExtra("FILE_NAME") ?: "Materi"
        val database = FirebaseDatabase.getInstance().getReference("ReadingProgress").child(userId)
        database.orderByChild("fileName").equalTo(fileName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) return
                    val id = database.push().key ?: return
                    database.child(id).setValue(
                        hashMapOf<String, Any>(
                            "fileName" to fileName,
                            "completed" to true,
                            "updatedAt" to System.currentTimeMillis()
                        )
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}