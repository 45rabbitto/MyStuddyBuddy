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
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.chatbot.activity.ChatbotActivity
import com.studdy.mystudybuddy.presentation.screens.recommendation.activity.AlurActivity
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity
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

    private var isGuest = false

    private var fileUri: Uri? = null
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        auth = FirebaseAuth.getInstance()

        val session =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        isGuest =
            session.getBoolean(
                "isGuest",
                false
            )

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
                    // MEMBERIKAN IZIN AKSES BACA SECARA LEGAL KE RINGKASAN_ACTIVITY
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            )
        }

        btnChatbot.setOnClickListener {
            if (fileUri == null) return@setOnClickListener

            startActivity(
                Intent(this, ChatbotActivity::class.java).apply {
                    putExtra("FILE_URI", fileUri.toString())
                    putExtra("FILE_NAME", fileName)
                    // MEMBERIKAN IZIN AKSES BACA SECARA LEGAL KE CHATBOT_ACTIVITY
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            )
        }
    }

    private fun pickFile() {
        // MENGGUNAKAN ACTION_OPEN_DOCUMENT AGAR MENDAPATKAN KUNCI IZIN AKSES FILE YANG SAH
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        launcher.launch(intent)
    }

    private val launcher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult

                fileUri = uri
                fileName = getFileName(uri)

                // MENGUNCI IZIN AKSES DARI ANDROID SYSTEM SECARA PERMANEN SELAMA APLIKASI BERJALAN
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                tvKosong.visibility = View.GONE
                fileContainer.removeAllViews()

                fileContainer.addView(
                    TextView(this).apply {
                        text = fileName
                        textSize = 15f

                        setPadding(20, 20, 20, 20)
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
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }

    private fun saveToHistory(file: String) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("History").child(userId)
        val historyId = database.push().key ?: return

        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val historyMap = hashMapOf<String, Any>("fileName" to file, "date" to date)

        database.child(historyId).setValue(historyMap)
    }

    private fun saveUploadedMaterial(materialName: String) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("UploadedMaterials").child(userId)
        val materialId = database.push().key ?: return

        val materialMap = hashMapOf<String, Any>(
            "fileName" to materialName,
            "timestamp" to System.currentTimeMillis()
        )
        database.child(materialId).setValue(materialMap)
    }

    private fun updateProgress(fileName: String) {
        val prefs = getSharedPreferences("progress_data", MODE_PRIVATE)
        val materiLama = prefs.getInt("materi_count", 0)

        prefs.edit()
            .putInt("materi_count", materiLama + 1)
            .putString("last_file", fileName)
            .apply()
    }

    private fun openAlur() {
        startActivity(
            Intent(this, AlurActivity::class.java).apply {
                putExtra("FILE_NAME", fileName)
                putExtra("FILE_URI", fileUri.toString())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        )
    }
}