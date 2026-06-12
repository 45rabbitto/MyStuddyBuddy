package com.studdy.mystudybuddy.presentation.screens.upload.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.chatbot.activity.ChatbotActivity
import com.studdy.mystudybuddy.presentation.screens.recommendation.activity.AlurActivity
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity
import com.studdy.mystudybuddy.utils.LoggingHelper
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.text.SimpleDateFormat
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var uploadContainer: LinearLayout
    private lateinit var fileContainer: LinearLayout
    private lateinit var tvKosong: TextView
    private lateinit var btnRingkasan: Button
    private lateinit var btnChatbot: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var isGuest = false
    private var fileUri: Uri? = null
    private var fileName: String? = null
    private var savedDocumentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PDFBoxResourceLoader.init(applicationContext)

        setContentView(R.layout.activity_upload)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val session = getSharedPreferences("user_session", MODE_PRIVATE)
        isGuest = session.getBoolean("isGuest", false)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        uploadContainer = findViewById(R.id.uploadContainer)
        fileContainer = findViewById(R.id.fileContainer)
        tvKosong = findViewById(R.id.tvKosong)
        btnRingkasan = findViewById(R.id.btnRingkasan)
        btnChatbot = findViewById(R.id.btnChatbot)

        btnRingkasan.isEnabled = false
        btnChatbot.isEnabled = false
    }

    private fun isLoggedIn(): Boolean {
        return auth.currentUser != null && !isGuest
    }

    private fun setupClickListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        uploadContainer.setOnClickListener {
            pickFile()
        }

        btnRingkasan.setOnClickListener {

            if (fileUri == null) return@setOnClickListener

            startActivity(
                Intent(this, RingkasanActivity::class.java).apply {
                    putExtra("FILE_URI", fileUri.toString())
                    putExtra("FILE_NAME", fileName)
                    putExtra("DOCUMENT_ID", savedDocumentId)
                }
            )
        }

        btnChatbot.setOnClickListener {

            if (fileUri == null) return@setOnClickListener

            startActivity(
                Intent(this, ChatbotActivity::class.java).apply {
                    putExtra("FILE_URI", fileUri.toString())
                    putExtra("FILE_NAME", fileName)
                    putExtra("DOCUMENT_ID", savedDocumentId)
                }
            )
        }
    }

    private fun pickFile() {

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        launcher.launch(intent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val uri = result.data?.data ?: return@registerForActivityResult

                fileUri = uri
                fileName = getFileName(uri)

                val pdfText = extractTextFromPdf(uri)

                if (pdfText.isNotBlank()) {
                    savePdfTextToFirestore(
                        fileName ?: "unknown.pdf",
                        pdfText
                    )
                } else {
                    Toast.makeText(
                        this,
                        "PDF tidak berhasil dibaca",
                        Toast.LENGTH_LONG
                    ).show()
                }

                tvKosong.visibility = View.GONE

                fileContainer.removeAllViews()

                fileContainer.addView(
                    TextView(this).apply {

                        text = fileName
                        textSize = 15f

                        setPadding(
                            20,
                            20,
                            20,
                            20
                        )

                        setBackgroundResource(R.drawable.kontainer)

                        setOnClickListener {
                            openAlur()
                        }
                    }
                )

                if (isLoggedIn()) {
                    saveUploadedMaterial(fileName ?: "")
                    saveToHistory(fileName ?: "")
                    updateProgress(fileName ?: "")
                }

                btnRingkasan.isEnabled = true
                btnChatbot.isEnabled = true
            }
        }

    private fun getFileName(uri: Uri): String {

        var name = "file.pdf"

        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {

            if (it.moveToFirst()) {

                val index =
                    it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (index >= 0) {
                    name = it.getString(index)
                }
            }
        }

        return name
    }

    private fun extractTextFromPdf(uri: Uri): String {

        return try {

            contentResolver.openInputStream(uri)?.use { inputStream ->

                val document = PDDocument.load(inputStream)

                val text = PDFTextStripper().getText(document)

                document.close()

                text

            } ?: ""

        } catch (e: Exception) {

            android.util.Log.e(
                "PDF_ERROR",
                e.stackTraceToString()
            )

            ""
        }
    }

    private fun savePdfTextToFirestore(
        fileName: String,
        content: String
    ) {

        val userId = auth.currentUser?.uid ?: "guest"

        val data = hashMapOf<String, Any>(
            "fileName" to fileName,
            "content" to content,
            "userId" to userId,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("PdfContents")
            .add(data)
            .addOnSuccessListener { documentRef ->

                savedDocumentId = documentRef.id

                android.util.Log.d(
                    "FIRESTORE",
                    "Document ID = ${documentRef.id}"
                )

                android.util.Log.d(
                    "FIRESTORE",
                    "Jumlah karakter = ${content.length}"
                )

                // ✅ LOGGING dengan fileName
                LoggingHelper.logTextLength(
                    documentId = savedDocumentId!!,
                    type = "pdfContents",
                    text = content,
                    userId = userId,
                    fileName = fileName,
                    originalLength = content.length,
                )

                Toast.makeText(
                    this,
                    "Isi PDF berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    this,
                    "Gagal menyimpan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun saveToHistory(file: String) {

        val userId = auth.currentUser?.uid ?: return

        val database =
            FirebaseDatabase.getInstance()
                .getReference("History")
                .child(userId)

        val historyId = database.push().key ?: return

        val date = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date())

        database.child(historyId).setValue(
            hashMapOf<String, Any>(
                "fileName" to file,
                "date" to date
            )
        )
    }

    private fun saveUploadedMaterial(materialName: String) {

        val userId = auth.currentUser?.uid ?: return

        val database =
            FirebaseDatabase.getInstance()
                .getReference("UploadedMaterials")
                .child(userId)

        val materialId = database.push().key ?: return

        database.child(materialId).setValue(
            hashMapOf<String, Any>(
                "fileName" to materialName,
                "timestamp" to System.currentTimeMillis()
            )
        )
    }

    private fun updateProgress(fileName: String) {

        val prefs =
            getSharedPreferences(
                "progress_data",
                MODE_PRIVATE
            )

        val materiLama =
            prefs.getInt("materi_count", 0)

        prefs.edit()
            .putInt(
                "materi_count",
                materiLama + 1
            )
            .putString(
                "last_file",
                fileName
            )
            .apply()
    }

    private fun openAlur() {

        startActivity(
            Intent(
                this,
                AlurActivity::class.java
            ).apply {

                putExtra(
                    "FILE_NAME",
                    fileName
                )

                putExtra(
                    "FILE_URI",
                    fileUri.toString()
                )

                putExtra(
                    "DOCUMENT_ID",
                    savedDocumentId
                )
            }
        )
    }
}