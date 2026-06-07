package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.utils.PDFReaderHelper
import com.studdy.mystudybuddy.utils.RingkasanAIHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnFinishRingkasan: Button

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var fileUri: String? = null
    private var currentSummary: String = ""
    private var currentExtractedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initViews()
        setupClickListeners()
        setupData()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnFinishRingkasan = findViewById(R.id.btnFinishRingkasan)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnFinishRingkasan.setOnClickListener {
            saveProgressMateri()
            Toast.makeText(this, "Materi selesai dipelajari", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnGenerate.setOnClickListener {
            val summary = tvRingkasan.text.toString()

            if (summary.isEmpty() || summary.contains("Belum") || summary.contains("sedang memproses") || summary.contains("Error")) {
                Toast.makeText(this, "Ringkasan belum siap atau gagal diproses", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan ke Firestore sebelum lanjut ke quiz
            saveSummaryToFirestore(currentSummary, currentExtractedText)

            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("RINGKASAN", currentSummary)
                putExtra("FILE_URI", fileUri)
            }
            startActivity(intent)
        }
    }

    private fun setupData() {
        fileUri = intent.getStringExtra("FILE_URI")
        val fileName = intent.getStringExtra("FILE_NAME") ?: "Dokumen"

        if (fileUri != null) {
            val parsedUri = Uri.parse(fileUri)

            // Set teks awal
            tvRingkasan.text = "📄 File: $fileName\n\n🔄 Mengekstrak teks dari PDF...\n🤖 AI MobileBERT akan meringkas..."

            // Jalankan proses di background
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // STEP 1: Ekstrak teks dari PDF pakai PDFBox
                    val pdfReader = PDFReaderHelper(this@RingkasanActivity)
                    val extractedText = pdfReader.ekstrakTeksDariPDF(parsedUri)

                    if (extractedText.startsWith("Gagal") || extractedText.startsWith("Error")) {
                        withContext(Dispatchers.Main) {
                            tvRingkasan.text = " $extractedText"
                        }
                        return@launch
                    }

                    currentExtractedText = extractedText

                    withContext(Dispatchers.Main) {
                        tvRingkasan.text = " File: $fileName\n\n✅ Teks berhasil diekstrak (${extractedText.length} karakter)\n\n🤖 AI MobileBERT sedang meringkas..."
                    }

                    // STEP 2: Kirim ke backend Python (MobileBERT ONNX)
                    val aiHelper = RingkasanAIHelper(this@RingkasanActivity)
                    val summary = aiHelper.prosesRingkasan(extractedText)

                    currentSummary = summary

                    // STEP 3: Tampilkan hasil di UI
                    withContext(Dispatchers.Main) {
                        tvRingkasan.text = """
                             File: $fileName
                            
                            🔹 RINGKASAN (AI MobileBERT):
                            
                            $summary
                            
                            ─────────────────────────────
                            📊 Statistik:
                            • Teks asli: ${extractedText.length} karakter
                            • Ringkasan: ${summary.length} karakter
                            • Model: MobileBERT ONNX (Lokal)
                        """.trimIndent()
                    }

                    // STEP 4: Simpan ke Firestore
                    saveSummaryToFirestore(summary, extractedText)

                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        tvRingkasan.text = " Error: ${e.message}\n\nTips: Pastikan backend Python berjalan di http://10.0.2.2:8000"
                    }
                }
            }

        } else {
            tvRingkasan.text = " Tidak ada file yang dikirim dari UploadActivity"
        }
    }

    /**
     * Simpan ke Firestore (bukan Realtime Database)
     */
    private fun saveSummaryToFirestore(summary: String, extractedText: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val documentData = hashMapOf(
            "summary" to summary,
            "extractedText" to extractedText.take(1000), // Batasi panjang
            "textLength" to extractedText.length,
            "summaryLength" to summary.length,
            "createdAt" to Date(),
            "userId" to userId
        )

        firestore.collection("summaries")
            .add(documentData)
            .addOnSuccessListener {
                // Berhasil disimpan
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal simpan ke Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProgressMateri() {
        val userId = auth.currentUser?.uid ?: return
        val fileName = intent.getStringExtra("FILE_NAME") ?: "Materi"

        val progressData = hashMapOf(
            "fileName" to fileName,
            "completed" to true,
            "updatedAt" to Date()
        )

        firestore.collection("readingProgress")
            .document("$userId")
            .collection("materi")
            .add(progressData)
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan progress", Toast.LENGTH_SHORT).show()
            }
    }
}