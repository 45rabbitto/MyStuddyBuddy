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
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity
import com.studdy.mystudybuddy.presentation.screens.chatbot.activity.ChatbotActivity

class UploadActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView

    private lateinit var uploadContainer: LinearLayout
    private lateinit var fileContainer: LinearLayout
    private lateinit var tvKosong: TextView

    private lateinit var btnRingkasan: Button
    private lateinit var btnChatbot: Button

    private var fileUri: Uri? = null
    private var hasFile: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        initViews()
        setupClickListeners()
        updateButtonState()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)

        uploadContainer = findViewById(R.id.uploadContainer)
        fileContainer = findViewById(R.id.fileContainer)
        tvKosong = findViewById(R.id.tvKosong)

        btnRingkasan = findViewById(R.id.btnRingkasan)
        btnChatbot = findViewById(R.id.btnChatbot)
    }

    private fun setupClickListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        uploadContainer.setOnClickListener {
            pickFile()
        }

        btnRingkasan.setOnClickListener {

            if (fileUri == null) {
                Toast.makeText(this, "Silakan upload file dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(
                this,
                com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity::class.java)

            intent.putExtra("FILE_URI", fileUri.toString())

            startActivity(intent)
        }

        btnChatbot.setOnClickListener {
            Toast.makeText(this, "Menu Chatbot", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonState() {
        btnRingkasan.isEnabled = hasFile
        btnChatbot.isEnabled = hasFile
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val uri = result.data?.data

                uri?.let {
                    fileUri = it
                    addFileToList(it)
                }
            }
        }

    private fun addFileToList(uri: Uri) {

        hasFile = true
        updateButtonState()

        tvKosong.visibility = View.GONE

        val fileName = getFileName(uri)

        val itemView = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
        }

        val textView = TextView(this).apply {
            text = fileName
            textSize = 14f
            setPadding(16, 16, 16, 16)
        }

        val deleteBtn = Button(this).apply {
            text = "Hapus"

            setOnClickListener {

                fileContainer.removeView(itemView)

                // kalau tidak ada file lagi
                if (fileContainer.childCount == 0) {
                    tvKosong.visibility = View.VISIBLE
                    hasFile = false
                    fileUri = null
                    updateButtonState()
                }
            }
        }

        itemView.addView(textView)
        itemView.addView(deleteBtn)

        fileContainer.addView(itemView)
    }

    private fun getFileName(uri: Uri): String {

        var fileName = "file.pdf"

        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {

            if (it.moveToFirst()) {

                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (index >= 0) {
                    fileName = it.getString(index)
                }
            }
        }

        return fileName
    }
}