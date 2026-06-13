package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.data.model.SummaryModel
import com.studdy.mystudybuddy.network.RetrofitClient
import com.studdy.mystudybuddy.network.SummarizeRequest
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import com.studdy.mystudybuddy.presentation.screens.quiz.bottomsheet.BottomGenerateKuis

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvTeksAsli: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnRingkasan: Button
    private lateinit var btnGenerateQuiz: Button

    private lateinit var btnFinishRingkasan: Button
    private lateinit var progressBar: android.widget.ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var currentDocumentId: String = ""
    private var currentFileName: String = ""
    private var currentOriginalText: String = ""
    private var currentSummary: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        currentFileName = intent.getStringExtra("FILE_NAME") ?: "Dokumen"

        initViews()
        setupClickListeners()

        loadOriginalText()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTeksAsli = findViewById(R.id.tvTeksAsli)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        btnRingkasan = findViewById(R.id.btnRingkasan)
        btnGenerateQuiz = findViewById(R.id.btnGenerateQuiz)
        btnFinishRingkasan = findViewById(R.id.btnFinishRingkasan)
        progressBar = findViewById(R.id.progressBar)

        btnRingkasan.isEnabled = false
        btnGenerateQuiz.isEnabled = false
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnRingkasan.setOnClickListener {
            if (currentOriginalText.isNotEmpty()) {
                generateSummary()
            } else {
                Toast.makeText(this, "Teks belum dimuat. Tunggu sebentar...", Toast.LENGTH_SHORT).show()
            }
        }


        btnGenerateQuiz.setOnClickListener {

            val bottomSheet =
                BottomGenerateKuis()

            bottomSheet.arguments =
                Bundle().apply {

                    putString(
                        "FILE_NAME",
                        currentFileName
                    )

                    putString(
                        "RINGKASAN",
                        currentSummary
                    )

                    putString(
                        "MATERI_ASLI",
                        currentOriginalText
                    )
                }

            bottomSheet.show(
                supportFragmentManager,
                "bottom_generate_kuis"
            )
        }

        btnFinishRingkasan.setOnClickListener {

            showFeedbackDialog()
        }
    }

    private fun showFeedbackDialog() {
        val dialogView = layoutInflater.inflate(R.layout.avtivity_feedback, null)

        val ivLike = dialogView.findViewById<ImageView>(R.id.ivLike)
        val ivDislike = dialogView.findViewById<ImageView>(R.id.ivDislike)
        val tvQuestion = dialogView.findViewById<TextView>(R.id.tvQuestion)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        ivLike.setOnClickListener {

            saveFeedbackToFirestore("like")

            updateProgress(70)

            Toast.makeText(
                this,
                "Terimaksih Atas Feedback Anda",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }

        ivDislike.setOnClickListener {

            saveFeedbackToFirestore("dislike")

            updateProgress(70)

            Toast.makeText(
                this,
                "Terima kasih atas feedback Anda",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveFeedbackToFirestore(feedback: String) {
        val userId = auth.currentUser?.uid ?: "guest"

        val data = hashMapOf(
            "feedback" to feedback,
            "documentId" to currentDocumentId,
            "fileName" to currentFileName,
            "summary" to currentSummary,
            "timestamp" to Date(),
            "userId" to userId
        )

        firestore.collection("feedbacks")
            .add(data)
            .addOnSuccessListener {
                Log.d("FEEDBACK", "Feedback saved: $feedback")
            }
            .addOnFailureListener { e ->
                Log.e("FEEDBACK", "Failed to save feedback: ${e.message}")
            }
    }

    private fun loadOriginalText() {

        tvTeksAsli.text =
            "Memuat dokumen..."

        showLoading(true)

        lifecycleScope.launch {

            try {

                val userId =
                    auth.currentUser?.uid ?: return@launch

                val snapshot =
                    firestore.collection("PdfContents")
                        .document(userId)
                        .collection("documents")
                        .whereEqualTo(
                            "fileName",
                            currentFileName
                        )
                        .get()
                        .await()

                if (snapshot.isEmpty) {

                    tvTeksAsli.text =
                        "❌ Dokumen tidak ditemukan"

                    showLoading(false)
                    return@launch
                }

                val doc =
                    snapshot.documents.first()

                currentDocumentId =
                    doc.id

                currentOriginalText =
                    doc.getString("content")
                        ?: ""

                if (currentOriginalText.isEmpty()) {

                    tvTeksAsli.text =
                        "❌ Isi dokumen kosong"

                    showLoading(false)
                    return@launch
                }

                tvTeksAsli.text =
                    currentOriginalText.take(1000)

                btnRingkasan.isEnabled =
                    true

                showLoading(false)

            } catch (e: Exception) {

                tvTeksAsli.text =
                    "❌ ${e.message}"

                showLoading(false)
            }
        }
    }

    private fun generateSummary() {
        tvRingkasan.text = "📡 Mengirim ke AI...\n\n AI sedang meringkas (mohon tunggu 5-10 detik)..."
        showLoading(true)
        btnRingkasan.isEnabled = false

        lifecycleScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: "guest"

                val response = RetrofitClient.api.summarizeText(
                    SummarizeRequest(currentOriginalText)
                )

                if (response.isSuccessful && response.body() != null) {
                    val summaryText = response.body()!!.summary
                    currentSummary = summaryText

                    val summaryModel = SummaryModel(
                        documentId = currentDocumentId,
                        fileName = currentFileName,
                        summary = summaryText,
                        summaryLength = summaryText.length,
                        originalLength = currentOriginalText.length,
                        createdAt = Date(),
                        userId = userId
                    )

                    val summaryRef = firestore.collection("summaries").document()
                    summaryRef.set(summaryModel).await()

                    firestore.collection("PdfContents")
                        .document(userId)
                        .collection("documents")
                        .document(currentDocumentId)
                        .update("summaryId", summaryRef.id, "isProcessed", true)
                        .await()

                    tvRingkasan.text = """
                        RINGKASAN AI:
                        
                        $summaryText
                        
                        ─────────────────────────
                        Statistik:
                        • Teks asli: ${currentOriginalText.length} karakter
                        • Ringkasan: ${summaryText.length} karakter
                    """.trimIndent()

                    Toast.makeText(this@RingkasanActivity, "✅ Ringkasan berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    btnGenerateQuiz.isEnabled = true

                } else {
                    tvRingkasan.text = "❌ Gagal membuat ringkasan: API error ${response.code()}"
                }

                showLoading(false)
                btnRingkasan.isEnabled = true

            } catch (e: Exception) {
                Log.e("Ringkasan", "Error: ${e.message}", e)
                tvRingkasan.text = "❌ Error: ${e.message}"
                showLoading(false)
                btnRingkasan.isEnabled = true
            }
        }
    }

    private fun saveProgressMateri() {

        val uid =
            FirebaseAuth.getInstance()
                .currentUser?.uid ?: return

        val safeFileName =
            currentFileName.replace(".", "_")

        FirebaseDatabase.getInstance()
            .reference
            .child("ReadingProgress")
            .child(uid)
            .child(safeFileName)
            .setValue(
                mapOf(
                    "fileName" to currentFileName,
                    "progress" to 70,
                    "completed" to true
                )
            )
    }

    private fun updateProgress(progress: Int) {

        val uid =
            FirebaseAuth.getInstance()
                .currentUser?.uid ?: return

        val safeFileName =
            currentFileName.replace(".", "_")

        FirebaseDatabase.getInstance()
            .reference
            .child("ReadingProgress")
            .child(uid)
            .child(safeFileName)
            .setValue(
                mapOf(
                    "fileName" to currentFileName,
                    "progress" to progress,
                    "completed" to true
                )
            )
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        btnRingkasan.isEnabled = !isLoading
        btnGenerateQuiz.isEnabled = !isLoading && currentSummary.isNotEmpty()
    }
}