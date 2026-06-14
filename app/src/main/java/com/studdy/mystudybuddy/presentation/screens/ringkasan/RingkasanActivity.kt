package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.studdy.mystudybuddy.network.RetrofitClient
import com.studdy.mystudybuddy.network.SummarizeRequest
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
        currentDocumentId = intent.getStringExtra("DOCUMENT_ID") ?: ""

        Log.d("DEBUG_FLOW", "fileName = $currentFileName")
        Log.d("DEBUG_FLOW", "documentId = $currentDocumentId")

        initViews()
        setupClickListeners()

        val fromHistory = intent.getBooleanExtra("FROM_HISTORY", false)

        if (currentDocumentId.isBlank()) {
            tvRingkasan.text = "Document ID tidak valid"
            return
        }

        if (fromHistory) {
            loadSavedSummary()
        } else {
            loadOriginalText()
        }
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
            if (currentSummary.isEmpty()) {
                generateSummary()
            } else {
                showSummary()
            }
        }

        btnGenerateQuiz.setOnClickListener {
            val bottomSheet = BottomGenerateKuis()

            bottomSheet.arguments = Bundle().apply {
                putString("FILE_NAME", currentFileName)
                putString("RINGKASAN", currentSummary)
                putString("MATERI_ASLI", currentOriginalText)
            }

            bottomSheet.show(supportFragmentManager, "bottom_generate_kuis")
        }

        btnFinishRingkasan.setOnClickListener {
            showFeedbackDialog()
        }
    }

    private fun showSummary() {
        tvRingkasan.text = """
            RINGKASAN AI:

            $currentSummary
        """.trimIndent()
    }

    private fun loadOriginalText() {
        tvTeksAsli.text = "Memuat dokumen..."
        showLoading(true)

        lifecycleScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch

                val snapshot = firestore.collection("PdfContents")
                    .document(uid)
                    .collection("documents")
                    .document(currentDocumentId)
                    .get()
                    .await()

                if (!snapshot.exists()) {
                    tvTeksAsli.text = "Dokumen tidak ditemukan"
                    showLoading(false)
                    return@launch
                }

                currentOriginalText = snapshot.getString("content") ?: ""
                currentSummary = snapshot.getString("summary") ?: ""

                tvTeksAsli.text = currentOriginalText

                if (currentSummary.isNotEmpty()) {
                    tvRingkasan.text = buildSummaryUI(currentSummary)
                }

                btnRingkasan.isEnabled = true

            } catch (e: Exception) {
                tvTeksAsli.text = "Error: ${e.message}"
            } finally {
                showLoading(false)
            }
        }
    }

    private fun loadSavedSummary() {
        val uid = auth.currentUser?.uid ?: return

        Log.d("SUMMARY_DEBUG", "uid = $uid")
        Log.d("SUMMARY_DEBUG", "documentId = $currentDocumentId")

        firestore.collection("PdfContents")
            .document(uid)
            .collection("documents")
            .document(currentDocumentId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    tvRingkasan.text = "Data tidak ditemukan"
                    return@addOnSuccessListener
                }

                currentOriginalText = doc.getString("content") ?: ""
                currentSummary = doc.getString("summary") ?: ""

                tvTeksAsli.text = currentOriginalText

                if (currentSummary.isNotEmpty()) {
                    tvRingkasan.text = buildSummaryUI(currentSummary)
                }

                btnRingkasan.isEnabled = true
                btnGenerateQuiz.isEnabled = currentSummary.isNotEmpty()
            }
            .addOnFailureListener {
                tvRingkasan.text = "Gagal load data"
            }
    }

    private fun generateSummary() {
        tvRingkasan.text = "📡 AI sedang meringkas..."
        showLoading(true)

        lifecycleScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch

                val response = RetrofitClient.api.summarizeText(
                    SummarizeRequest(currentOriginalText)
                )

                if (response.isSuccessful && response.body() != null) {

                    val summaryText = response.body()!!.summary
                    currentSummary = summaryText

                    firestore.collection("PdfContents")
                        .document(uid)
                        .collection("documents")
                        .document(currentDocumentId)
                        .update(
                            mapOf(
                                "summary" to summaryText,
                                "isProcessed" to true
                            )
                        )
                        .await()

                    tvRingkasan.text = buildSummaryUI(summaryText)
                    btnGenerateQuiz.isEnabled = true
                }

            } catch (e: Exception) {
                tvRingkasan.text = "Error: ${e.message}"
            } finally {
                showLoading(false)
                btnRingkasan.isEnabled = true
            }
        }
    }

    private fun buildSummaryUI(summary: String): String {
        return """
            RINGKASAN AI:

            $summary

            ───────────────
            📊 Statistik:
            • Panjang teks: ${currentOriginalText.length}
            • Ringkasan: ${summary.length}
        """.trimIndent()
    }

    private fun showFeedbackDialog() {
        val dialogView = layoutInflater.inflate(R.layout.avtivity_feedback, null)

        val ivLike = dialogView.findViewById<ImageView>(R.id.ivLike)
        val ivDislike = dialogView.findViewById<ImageView>(R.id.ivDislike)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        ivLike.setOnClickListener {
            saveFeedbackToFirestore("like")
            updateProgress(70)
            Toast.makeText(this, "Terima kasih!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        ivDislike.setOnClickListener {
            saveFeedbackToFirestore("dislike")
            updateProgress(70)
            Toast.makeText(this, "Terima kasih!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveFeedbackToFirestore(feedback: String) {
        val uid = auth.currentUser?.uid ?: "guest"

        val data = hashMapOf(
            "feedback" to feedback,
            "documentId" to currentDocumentId,
            "fileName" to currentFileName,
            "summary" to currentSummary,
            "timestamp" to Date(),
            "userId" to uid
        )

        firestore.collection("feedbacks").add(data)
    }

    private fun updateProgress(progress: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .reference
            .child("ReadingProgress")
            .child(uid)
            .child(currentFileName.replace(".", "_"))
            .setValue(
                mapOf(
                    "fileName" to currentFileName,
                    "progress" to progress,
                    "completed" to true
                )
            )
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        btnRingkasan.isEnabled = !isLoading
        btnGenerateQuiz.isEnabled = !isLoading && currentSummary.isNotEmpty()
    }
}