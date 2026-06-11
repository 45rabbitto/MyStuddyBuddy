package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.data.repository.SummaryRepository
import com.studdy.mystudybuddy.presentation.screens.chatbot.activity.ChatbotActivity
import com.studdy.mystudybuddy.utils.PDFUtils
import kotlinx.coroutines.launch
import java.util.Date

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnFinishRingkasan: Button
    private lateinit var progressBar: android.widget.ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: SummaryRepository

    private var currentSummary: String = ""
    private var currentDocumentId: String = ""
    private var currentFileName: String = ""

    // PICK PDF
    private val pickPdf = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            currentFileName = uri.lastPathSegment ?: "document.pdf"
            processPdf(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        repository = SummaryRepository()

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnFinishRingkasan = findViewById(R.id.btnFinishRingkasan)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnGenerate.setOnClickListener {
            pickPdf.launch("application/pdf")
        }

        btnFinishRingkasan.setOnClickListener {
            saveProgressMateri()
            Toast.makeText(this, "Materi selesai dipelajari", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun processPdf(uri: Uri) {
        tvRingkasan.text = " Membaca file PDF: $currentFileName\n\n Mengekstrak teks..."
        showLoading(true)

        lifecycleScope.launch {
            try {
                val extractedText = PDFUtils.extractTextFromPdf(this@RingkasanActivity, uri)

                if (extractedText.isEmpty()) {
                    tvRingkasan.text = " Gagal mengekstrak teks dari PDF."
                    showLoading(false)
                    return@launch
                }

                tvRingkasan.text = " Teks berhasil diekstrak (${extractedText.length} karakter)\n\n AI sedang meringkas..."

                val userId = auth.currentUser?.uid ?: "user_123"

                val documentId = repository.saveDocument(currentFileName, extractedText, userId)
                currentDocumentId = documentId

                val result = repository.processAndSaveSummary(documentId)

                result.onSuccess { summaryModel ->
                    currentSummary = summaryModel.summary
                    tvRingkasan.text = """
                        🔹 RINGKASAN AI (MobileBERT):
                        
                        ${summaryModel.summary}
                        
                        ─────────────────────────
                         Statistik:
                        • Teks asli: ${summaryModel.originalLength} karakter
                        • Ringkasan: ${summaryModel.summaryLength} karakter
                        • Model: MobileBERT ONNX (Railway)
                    """.trimIndent()

                    Toast.makeText(this@RingkasanActivity, "Ringkasan berhasil dibuat!", Toast.LENGTH_SHORT).show()

                }.onFailure { error ->
                    tvRingkasan.text = "❌Gagal membuat ringkasan: ${error.message}"
                    Toast.makeText(this@RingkasanActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }

                showLoading(false)

            } catch (e: Exception) {
                e.printStackTrace()
                tvRingkasan.text = "❌ Error: ${e.message}"
                showLoading(false)
            }
        }
    }

    private fun saveProgressMateri() {
        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "fileName" to currentFileName,
            "completed" to true,
            "updatedAt" to Date(),
            "summaryId" to currentDocumentId
        )

        firestore.collection("readingProgress")
            .document(userId)
            .collection("materi")
            .add(data)
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan progress", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        btnGenerate.isEnabled = !isLoading
    }
}