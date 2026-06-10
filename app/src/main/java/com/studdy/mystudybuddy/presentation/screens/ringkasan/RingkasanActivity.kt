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
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.data.model.SummaryModel
import com.studdy.mystudybuddy.network.RetrofitClient
import com.studdy.mystudybuddy.network.SummarizeRequest
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

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

        currentDocumentId = intent.getStringExtra("DOCUMENT_ID") ?: ""
        currentFileName = intent.getStringExtra("FILE_NAME") ?: "Dokumen"

        if (currentDocumentId.isEmpty()) {
            Toast.makeText(this, "Error: Document ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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
            if (currentSummary.isNotEmpty()) {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("RINGKASAN", currentSummary)
                intent.putExtra("FILE_NAME", currentFileName)
                intent.putExtra("DOCUMENT_ID", currentDocumentId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ringkasan belum dibuat. Klik tombol RINGKASAN dulu!", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔥 TOMBOL FINISH - MUNCULKAN DIALOG FEEDBACK (LIKE/DISLIKE)
        btnFinishRingkasan.setOnClickListener {
            saveProgressMateri()
            showFeedbackDialog()
        }
    }

    // 🔥 MUNCULKAN DIALOG FEEDBACK (LIKE/DISLIKE DENGAN ImageView)
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
            Toast.makeText(this, "✅ Terima kasih atas masukan baiknya!", Toast.LENGTH_LONG).show()
            dialog.dismiss()
            finish()
        }

        ivDislike.setOnClickListener {
            saveFeedbackToFirestore("dislike")
            Toast.makeText(this, "🙏 Maaf atas pengalaman Anda. Kami akan perbaiki!", Toast.LENGTH_LONG).show()
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    // 🔥 SIMPAN FEEDBACK KE FIRESTORE
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

    // 🔥 AMBIL TEKS ASLI DARI FIRESTORE
    private fun loadOriginalText() {
        tvTeksAsli.text = "Membaca teks dari database...\n\n Memuat teks asli..."
        showLoading(true)

        lifecycleScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: "guest"

                val docSnapshot = firestore.collection("PdfContents")
                    .document(userId)
                    .collection("documents")
                    .document(currentDocumentId)
                    .get()
                    .await()

                if (docSnapshot.exists()) {
                    currentOriginalText = docSnapshot.getString("content") ?: ""

                    if (currentOriginalText.isNotEmpty()) {
                        val previewText = if (currentOriginalText.length > 800) {
                            currentOriginalText.take(800) + "..."
                        } else {
                            currentOriginalText
                        }
                        tvTeksAsli.text = """
                            TEKS ASLI (${currentOriginalText.length} karakter):
                            
                            $previewText
                            
                            ─────────────────────────
                            ✅ Teks siap. Klik tombol RINGKASAN untuk meringkas.
                        """.trimIndent()

                        btnRingkasan.isEnabled = true
                        Toast.makeText(this@RingkasanActivity, "✅ Teks berhasil dimuat!", Toast.LENGTH_SHORT).show()
                    } else {
                        tvTeksAsli.text = "❌ Teks kosong. Upload ulang PDF."
                        btnRingkasan.isEnabled = false
                    }
                } else {
                    tvTeksAsli.text = "❌ Dokumen tidak ditemukan di database."
                    btnRingkasan.isEnabled = false
                }

                showLoading(false)

            } catch (e: Exception) {
                Log.e("Ringkasan", "Error loading text: ${e.message}", e)
                tvTeksAsli.text = "❌ Error: ${e.message}"
                showLoading(false)
                btnRingkasan.isEnabled = false
            }
        }
    }

    // 🔥 GENERATE RINGKASAN DARI API RAILWAY
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

    // 🔥 SIMPAN PROGRESS KE READING PROGRESS (FIRESTORE)
    private fun saveProgressMateri() {
        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "fileName" to currentFileName,
            "completed" to true,
            "updatedAt" to Date(),
            "summaryId" to currentDocumentId
        )

        firestore.collection("PdfContents")
            .document(userId)
            .collection("readingProgress")
            .add(data)
            .addOnSuccessListener {
                Log.d("Ringkasan", "Progress saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Ringkasan", "Failed to save progress: ${e.message}")
                Toast.makeText(this, "Gagal menyimpan progress", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔥 TAMPILKAN LOADING
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        btnRingkasan.isEnabled = !isLoading
        btnGenerateQuiz.isEnabled = !isLoading && currentSummary.isNotEmpty()
    }
}