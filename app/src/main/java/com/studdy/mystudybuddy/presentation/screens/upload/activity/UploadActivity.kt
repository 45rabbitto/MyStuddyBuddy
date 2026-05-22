package com.studdy.mystudybuddy.presentation.screens.upload.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.chatbot.activity.ChatbotActivity
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

    private var fileUri: Uri? = null
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

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

    private fun setupClickListeners() {

        btnBack.setOnClickListener { finish() }

        uploadContainer.setOnClickListener { pickFile() }

        btnRingkasan.setOnClickListener {
            if (fileUri != null) {
                saveData()
                startActivity(Intent(this, RingkasanActivity::class.java).apply {
                    putExtra("FILE_URI", fileUri.toString())
                })
            }
        }

        btnChatbot.setOnClickListener {
            if (fileUri != null) {
                saveData()
                startActivity(Intent(this, ChatbotActivity::class.java).apply {
                    putExtra("FILE_URI", fileUri.toString())
                })
            }
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

                tvKosong.visibility = View.GONE

                fileContainer.removeAllViews()
                fileContainer.addView(TextView(this).apply {
                    text = fileName
                    textSize = 14f
                })

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
                if (index >= 0) name = it.getString(index)
            }
        }

        return name
    }

    // ===================== SAVE DATA =====================
    private fun saveData() {

        val progress = getSharedPreferences("progress_data", MODE_PRIVATE)
        val history = getSharedPreferences("history_data", MODE_PRIVATE)

        val time = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        // progress update
        val materi = progress.getInt("materi_count", 0)

        progress.edit()
            .putInt("materi_count", materi + 1)
            .putString("last_file", fileName)
            .apply()

        // history save
        val old = history.getStringSet("files", mutableSetOf()) ?: mutableSetOf()
        val newSet = HashSet(old)

        newSet.add("$fileName|$time")

        history.edit()
            .putStringSet("files", newSet)
            .apply()
    }
}