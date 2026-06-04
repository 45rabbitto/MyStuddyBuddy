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
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// Import kedua helper
import com.studdy.mystudybuddy.utils.PDFReaderHelper
import com.studdy.mystudybuddy.utils.RingkasanAIHelper

// Import Coroutines untuk pemrosesan background thread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnFinishRingkasan: Button

    // Firebase
    private lateinit var auth: FirebaseAuth

    private var fileUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        // Firebase
        auth = FirebaseAuth.getInstance()

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
            val text = tvRingkasan.text.toString()

            if (text.isEmpty() || text.contains("Belum") || text.contains("sedang memproses")) {
                Toast.makeText(this, "Ringkasan belum siap atau gagal diproses", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveSummaryToFirebase(text)

            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("RINGKASAN", text)
                putExtra("FILE_URI", fileUri)
            }
            startActivity(intent)
        }
    }

    private fun setupData() {
        fileUri = intent.getStringExtra("FILE_URI")

        if (fileUri != null) {
            val parsedUri = Uri.parse(fileUri)
            val fileName = parsedUri.lastPathSegment ?: "File PDF"

            // 1. Set teks awal di Main Thread untuk memberi tahu user proses sedang berjalan
            tvRingkasan.text = "File berhasil diterima: $fileName\n\n⌛ AI sedang memproses teks PDF di latar belakang..."

            // 2. Jalankan proses pembacaan PDF dan Model ONNX di Background Thread (Dispatchers.IO)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Inisialisasi helper di background
                    val pdfReader = PDFReaderHelper(this@RingkasanActivity)
                    val teksAsliPDF = pdfReader.ekstrakTeksDariPDF(parsedUri)

                    // Jika ekstraksi teks PDF gagal
                    if (teksAsliPDF.startsWith("Gagal") || teksAsliPDF.startsWith("PDF berhasil dibaca, tetapi tidak ditemukan teks")) {
                        // Kembalikan ke Main Thread untuk update tampilan eror
                        withContext(Dispatchers.Main) {
                            tvRingkasan.text = teksAsliPDF
                        }
                        return@launch
                    }

                    // Inisialisasi dan jalankan Model AI (Dual Model: Encoder & Decoder) di background
                    val aiHelper = RingkasanAIHelper(this@RingkasanActivity)
                    val hasilRingkasanAI = aiHelper.prosesRingkasan(teksAsliPDF)

                    // 3. Kembalikan hasil pemrosesan ke Main Thread (UI) agar teks di emulator berubah
                    withContext(Dispatchers.Main) {
                        tvRingkasan.text = """
                            File berhasil diterima:
                            
                            Nama file: $fileName
                            
                            🔹 Hasil Ringkasan AI:
                            $hasilRingkasanAI
                        """.trimIndent()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Tangkap eror memori / crash dan tampilkan aman di UI tanpa membuat aplikasi mati
                    withContext(Dispatchers.Main) {
                        tvRingkasan.text = "Eror pemrosesan memori: ${e.localizedMessage}\n\nTips: Jika terus berlanjut, naikkan alokasi RAM emulator Anda di Android Studio."
                    }
                }
            }

        } else {
            tvRingkasan.text = "Tidak ada file yang dikirim dari UploadActivity"
        }
    }

    private fun saveSummaryToFirebase(summaryText: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().getReference("Summaries").child(userId)
        val summaryId = database.push().key
        if (summaryId == null) {
            Toast.makeText(this, "Gagal membuat ID", Toast.LENGTH_SHORT).show()
            return
        }

        val summaryMap = HashMap<String, Any>()
        summaryMap["summaryText"] = summaryText
        summaryMap["createdAt"] = System.currentTimeMillis()

        database.child(summaryId).setValue(summaryMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Ringkasan berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan ringkasan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProgressMateri() {
        val userId = auth.currentUser?.uid ?: return
        val fileName = intent.getStringExtra("FILE_NAME") ?: Uri.parse(fileUri).lastPathSegment ?: "Materi"
        val database = FirebaseDatabase.getInstance().getReference("ReadingProgress").child(userId)

        database.orderByChild("fileName").equalTo(fileName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(this@RingkasanActivity, "Materi sudah selesai dipelajari", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val id = database.push().key ?: return
                    val progressMap = HashMap<String, Any>()
                    progressMap["fileName"] = fileName
                    progressMap["completed"] = true
                    progressMap["updatedAt"] = System.currentTimeMillis()

                    database.child(id).setValue(progressMap)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}