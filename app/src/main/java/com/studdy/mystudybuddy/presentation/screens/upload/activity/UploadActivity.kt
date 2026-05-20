package com.studdy.mystudybuddy.presentation.screens.upload.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystuddybuddy.presentation.screens.upload.adapter.UploadAdapter
import com.studdy.mystuddybuddy.presentation.screens.upload.model.UploadFile
import com.studdy.mystudybuddy.R

class UploadActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var uploadContainer: ImageView
    private lateinit var fileContainer: LinearLayout
    private lateinit var tvKosong: TextView

    private lateinit var btnRingkasan: Button
    private lateinit var btnChatbot: Button

    private lateinit var adapter: UploadAdapter
    private val fileList = mutableListOf<UploadFile>()
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        // INIT VIEW
        btnBack = findViewById(R.id.btnBack)
        uploadContainer = findViewById(R.id.uploadContainer)
        fileContainer = findViewById(R.id.fileContainer)
        tvKosong = findViewById(R.id.tvKosong)

        btnRingkasan = findViewById(R.id.btnRingkasan)
        btnChatbot = findViewById(R.id.btnChatbot)

        // BACK BUTTON
        btnBack.setOnClickListener {
            finish()
        }

        // CLICK AREA UPLOAD
        uploadContainer.setOnClickListener {
            pickFile()
        }

        // BUTTON NAVIGASI BAWAH
        btnRingkasan.setOnClickListener {
            Toast.makeText(this, "Menu Ringkasan", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, RingkasanActivity::class.java))
        }

        btnChatbot.setOnClickListener {
            Toast.makeText(this, "Menu Chatbot", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, ChatbotActivity::class.java))
        }
    }

    /**
     * PICK FILE (PDF ONLY)
     */
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    fileUri = uri
                    addFileToList(uri)
                }
            }
        }

    private fun addFileToList(uri: Uri) {

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

                if (fileContainer.childCount == 1) {
                    tvKosong.visibility = View.VISIBLE
                }
            }
        }

        itemView.addView(textView)
        itemView.addView(deleteBtn)

        fileContainer.addView(itemView)
    }


    /**
     * AMBIL NAMA FILE
     */
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
}