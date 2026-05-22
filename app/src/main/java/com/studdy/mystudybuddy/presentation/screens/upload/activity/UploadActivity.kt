package com.studdy.mystudybuddy.presentation.screens.upload.activity

import android.app.Activity
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

    private var fileUri: Uri? = null
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        initViews()
        setupClickListeners()

        // Simpan daftar materi upload default
        saveUploadedMaterials()
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

        btnBack.setOnClickListener {
            finish()
        }

        uploadContainer.setOnClickListener {
            pickFile()
        }

        btnRingkasan.setOnClickListener {

            if (fileUri == null) return@setOnClickListener

            saveToHistory("RINGKASAN")

            startActivity(
                Intent(this, RingkasanActivity::class.java).apply {
                    putExtra("FILE_URI", fileUri.toString())
                    putExtra("FILE_NAME", fileName)
                }
            )
        }

        btnChatbot.setOnClickListener {

            if (fileUri == null) return@setOnClickListener

            saveToHistory("CHATBOT")

            startActivity(
                Intent(this, ChatbotActivity::class.java).apply {
                    putExtra("FILE_URI", fileUri.toString())
                    putExtra("FILE_NAME", fileName)
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

                tvKosong.visibility = View.GONE

                fileContainer.removeAllViews()

                fileContainer.addView(TextView(this).apply {

                    text = fileName
                    textSize = 15f

                    setPadding(20, 20, 20, 20)

                    setBackgroundResource(R.drawable.kontainer)

                    setOnClickListener {
                        openAlur()
                    }
                })

                // Simpan materi upload terbaru
                saveUploadedMaterial(fileName ?: "")

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

    private fun saveToHistory(type: String) {

        val prefs = getSharedPreferences("history_data", MODE_PRIVATE)

        val old = prefs.getStringSet("files", mutableSetOf()) ?: mutableSetOf()

        val newSet = HashSet(old)

        val date = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date())

        newSet.add("$fileName|$date|$type")

        prefs.edit()
            .putStringSet("files", newSet)
            .apply()
    }

    // Simpan daftar materi bawaan
    private fun saveUploadedMaterials() {

        // ===== history_data =====
        val historyPrefs = getSharedPreferences("history_data", MODE_PRIVATE)

        val historyList = mutableSetOf(
            "Deep Learning.pdf",
            "Python AI.pdf"
        )

        historyPrefs.edit()
            .putStringSet("uploaded_materials", historyList)
            .apply()

        // ===== progress_data =====
        val progressPrefs = getSharedPreferences("progress_data", MODE_PRIVATE)

        val progressList = mutableSetOf(
            "Deep Learning.pdf",
            "Python AI.pdf"
        )

        progressPrefs.edit()
            .putStringSet("uploaded_materials", progressList)
            .apply()
    }

    // Simpan materi upload baru
    private fun saveUploadedMaterial(materialName: String) {

        // ===== history_data =====
        val historyPrefs = getSharedPreferences("history_data", MODE_PRIVATE)

        val historyList =
            historyPrefs.getStringSet(
                "uploaded_materials",
                mutableSetOf()
            )?.toMutableSet() ?: mutableSetOf()

        historyList.add(materialName)

        historyPrefs.edit()
            .putStringSet("uploaded_materials", historyList)
            .apply()

        // ===== progress_data =====
        val progressPrefs = getSharedPreferences("progress_data", MODE_PRIVATE)

        val progressList =
            progressPrefs.getStringSet(
                "uploaded_materials",
                mutableSetOf()
            )?.toMutableSet() ?: mutableSetOf()

        progressList.add(materialName)

        progressPrefs.edit()
            .putStringSet("uploaded_materials", progressList)
            .apply()
    }

    private fun openAlur() {

        if (fileUri == null) return

        startActivity(
            Intent(this, AlurActivity::class.java).apply {
                putExtra("FILE_NAME", fileName)
                putExtra("FILE_URI", fileUri.toString())
            }
        )
    }
}