package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnFinishRingkasan: Button

    // Firebase
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    private var fileUri: String? = null
    private var currentSummaryText: String = ""  // Menyimpan ringkasan saat ini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        // Firebase
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
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnFinishRingkasan.setOnClickListener {
            showFeedbackDialog()
        }

        btnGenerate.setOnClickListener {
            val text = tvRingkasan.text.toString()

            if (text.isEmpty() || text.contains("Belum")) {
                Toast.makeText(this, "Ringkasan belum tersedia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan ringkasan
            currentSummaryText = text

            // Tampilkan dialog untuk memilih jumlah soal
            showJumlahSoalDialog()
        }
    }

    // ===================================
    // DIALOG PILIH JUMLAH SOAL
    // ===================================
    private fun showJumlahSoalDialog() {
        // Inflate layout dialog
        val dialogView = layoutInflater.inflate(R.layout.bottom_generate_kuis, null)

        // Inisialisasi view komponen dialog
        val tvJumlahSoal = dialogView.findViewById<TextView>(R.id.tvJumlahSoal)
        val btnMinus = dialogView.findViewById<Button>(R.id.btnMinus)
        val btnPlus = dialogView.findViewById<Button>(R.id.btnPlus)
        val btnStartQuiz = dialogView.findViewById<Button>(R.id.btnStartQuiz)

        var jumlahSoal = 5 // Default 5 soal

        tvJumlahSoal.text = jumlahSoal.toString()

        // Tombol Minus (-)
        btnMinus.setOnClickListener {
            if (jumlahSoal > 3) {
                jumlahSoal--
                tvJumlahSoal.text = jumlahSoal.toString()
            } else {
                Toast.makeText(this, "Minimal 3 soal", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Plus (+)
        btnPlus.setOnClickListener {
            if (jumlahSoal < 20) {
                jumlahSoal++
                tvJumlahSoal.text = jumlahSoal.toString()
            } else {
                Toast.makeText(this, "Maksimal 20 soal", Toast.LENGTH_SHORT).show()
            }
        }

        // Buat AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Tombol Mulai Kuis
        btnStartQuiz.setOnClickListener {
            dialog.dismiss()

            // Simpan ringkasan ke Firebase Realtime Database
            saveSummaryToFirebase(currentSummaryText)

            // Pindah ke QuizActivity dengan membawa data
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("RINGKASAN", currentSummaryText)
                putExtra("FILE_URI", fileUri)
                putExtra("FILE_NAME", intent.getStringExtra("FILE_NAME"))
                putExtra("JUMLAH_SOAL", jumlahSoal)
            }
            startActivity(intent)
        }

        dialog.show()

        // Atur agar background dialog transparan (opsional)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun loadPdfData() {
        val documentId = intent.getStringExtra("DOCUMENT_ID")
        val fileName = intent.getStringExtra("FILE_NAME")
        val pdfText = intent.getStringExtra("PDF_TEXT")

        // Prioritas: jika ada PDF_TEXT dari Intent (cara lama)
        if (!pdfText.isNullOrEmpty()) {
            displayPdfContent(fileName ?: "File PDF", pdfText)
            return
        }

        // Jika tidak ada PDF_TEXT tapi ada DOCUMENT_ID, ambil dari Firestore
        if (documentId != null) {
            loadPdfTextFromFirestore(documentId)
        } else if (fileName != null) {
            // Fallback: hanya tampilkan nama file
            tvRingkasan.text = """
                Nama File:
                $fileName

                Isi PDF:
                (Teks belum tersedia)
            """.trimIndent()
        }
    }

    private fun loadPdfTextFromFirestore(documentId: String) {
        firestore.collection("PdfContents")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val content = document.getString("content") ?: ""
                    val fileName = document.getString("fileName") ?: "File PDF"
                    displayPdfContent(fileName, content)
                } else {
                    tvRingkasan.text = "Dokumen tidak ditemukan"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat teks: ${e.message}", Toast.LENGTH_LONG).show()
                tvRingkasan.text = "Gagal memuat teks PDF"
            }
    }

    private fun displayPdfContent(fileName: String, content: String) {
        tvRingkasan.text = """
            Nama File:
            $fileName

            Isi PDF:
            ${content.take(3000)}
        """.trimIndent()
    }

    private fun showFeedbackDialog() {
        val view = layoutInflater.inflate(R.layout.avtivity_feedback, null)

        val ivLike = view.findViewById<ImageView>(R.id.ivLike)
        val ivDislike = view.findViewById<ImageView>(R.id.ivDislike)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        ivLike.setOnClickListener {
            saveFeedback("LIKE")
            saveProgressMateri()
            Toast.makeText(this, "Terima kasih atas feedback Anda 😊", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        ivDislike.setOnClickListener {
            saveFeedback("DISLIKE")
            saveProgressMateri()
            Toast.makeText(this, "Feedback tersimpan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    // ===================================
    // SIMPAN RINGKASAN KE FIREBASE RTDB
    // ===================================
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
                // Toast sudah ditampilkan di tombol generate
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan ringkasan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveFeedback(feedback: String) {
        val userId = auth.currentUser?.uid ?: return

        val fileName = intent.getStringExtra("FILE_NAME")
            ?: Uri.parse(fileUri).lastPathSegment
            ?: "Materi"

        val database = FirebaseDatabase.getInstance().getReference("Feedback").child(userId)
        val feedbackId = database.push().key ?: return

        val feedbackMap = HashMap<String, Any>()
        feedbackMap["feedback"] = feedback
        feedbackMap["fileName"] = fileName
        feedbackMap["createdAt"] = System.currentTimeMillis()

        database.child(feedbackId).setValue(feedbackMap)
    }

    // ===================================
    // SIMPAN PROGRESS MATERI
    // ===================================
    private fun saveProgressMateri() {
        val userId = auth.currentUser?.uid ?: return

        val fileName = intent.getStringExtra("FILE_NAME")
            ?: Uri.parse(fileUri).lastPathSegment
            ?: "Materi"

        val database = FirebaseDatabase.getInstance().getReference("ReadingProgress").child(userId)

        // cek apakah file sudah ada
        database.orderByChild("fileName")
            .equalTo(fileName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(
                            this@RingkasanActivity,
                            "Materi sudah selesai dipelajari",
                            Toast.LENGTH_SHORT
                        ).show()
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