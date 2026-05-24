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
import java.util.Date
import java.util.Locale

class UploadActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var uploadContainer: LinearLayout
    private lateinit var fileContainer: LinearLayout
    private lateinit var tvKosong: TextView
    private lateinit var btnRingkasan: Button
    private lateinit var btnChatbot: Button

    // Firebase
    private lateinit var auth: FirebaseAuth

    private var fileUri: Uri? = null
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upload)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()

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

                // Simpan ke Firebase
                saveUploadedMaterial(fileName ?: "")

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

    // =========================
    // SIMPAN HISTORY FIREBASE
    // =========================

    private fun saveToHistory(type: String) {

        val userId = auth.currentUser?.uid ?: return

        val database = FirebaseDatabase
            .getInstance()
            .getReference("History")
            .child(userId)

        val historyId = database.push().key ?: return

        val date = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date())

        val historyMap = HashMap<String, Any>()

        historyMap["fileName"] = fileName ?: ""
        historyMap["date"] = date
        historyMap["type"] = type

        database.child(historyId)
            .setValue(historyMap)
    }

    // =========================
    // SIMPAN FILE FIREBASE
    // =========================

    private fun saveUploadedMaterial(materialName: String) {

        val userId = auth.currentUser?.uid ?: return

        val database = FirebaseDatabase
            .getInstance()
            .getReference("UploadedMaterials")
            .child(userId)

        val materialId = database.push().key ?: return

        val materialMap = HashMap<String, Any>()

        materialMap["fileName"] = materialName
        materialMap["timestamp"] =
            System.currentTimeMillis()

        database.child(materialId)
            .setValue(materialMap)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "File berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal upload data",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun openAlur() {

        if (fileUri == null) return

        startActivity(
            Intent(this, AlurActivity::class.java).apply {

                putExtra(
                    "FILE_NAME",
                    fileName
                )

                putExtra(
                    "FILE_URI",
                    fileUri.toString()
                )
            }
        )
    }
}