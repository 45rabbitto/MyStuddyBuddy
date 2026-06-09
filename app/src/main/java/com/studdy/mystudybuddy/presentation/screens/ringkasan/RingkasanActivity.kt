package com.studdy.mystudybuddy.presentation.screens.ringkasan

import com.studdy.mystudybuddy.R
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
import com.studdy.mystudybuddy.network.RetrofitClient
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Date

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnFinishRingkasan: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var fileUri: Uri? = null
    private var currentSummary: String = ""

    // 🔥 PICK PDF LANGSUNG DI SINI
    private val pickPdf =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                fileUri = uri
                uploadPdfToServer(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initViews()
        setupClickListeners()
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

        // 🔥 PILIH PDF
        btnGenerate.setOnClickListener {
            pickPdf.launch("application/pdf")
        }

        btnFinishRingkasan.setOnClickListener {
            saveProgressMateri()
            Toast.makeText(this, "Materi selesai dipelajari", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 🔥 UPLOAD KE RAILWAY
    private fun uploadPdfToServer(uri: Uri) {

        tvRingkasan.text = "📤 Mengupload PDF ke server..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {

                val file = uriToFile(uri)

                val requestFile = file.asRequestBody("application/pdf".toMediaType())
                val body = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )

                val response = RetrofitClient.api.summarizePdf(body)

                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {

                        val summary = response.body()?.summary ?: "Tidak ada hasil"
                        currentSummary = summary

                        tvRingkasan.text = """
                            🔹 RINGKASAN AI (RAILWAY):
                            
                            $summary
                        """.trimIndent()

                        // simpan ke firestore
                        saveSummaryToFirestore(summary)

                    } else {
                        tvRingkasan.text = "❌ Gagal dari server"
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    tvRingkasan.text = "❌ Error: ${e.message}"
                }
            }
        }
    }

    // 🔥 Uri → File
    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp.pdf")

        file.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        inputStream?.close()
        return file
    }

    private fun saveSummaryToFirestore(summary: String) {

        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "summary" to summary,
            "createdAt" to Date(),
            "userId" to userId
        )

        firestore.collection("summaries")
            .add(data)
    }

    private fun saveProgressMateri() {

        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "completed" to true,
            "updatedAt" to Date()
        )

        firestore.collection("readingProgress")
            .document(userId)
            .set(data)
    }
}