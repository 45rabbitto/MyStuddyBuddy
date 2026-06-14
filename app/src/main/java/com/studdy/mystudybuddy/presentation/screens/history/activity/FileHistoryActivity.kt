package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.history.adapter.FileHistoryAdapter
import com.studdy.mystudybuddy.presentation.screens.history.model.FileHistoryModel
import android.util.Log

class FileHistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var btnBack: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val historyList = mutableListOf<FileHistoryModel>()

    private var isGuest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_file)

        val session = getSharedPreferences("user_session", MODE_PRIVATE)
        isGuest = session.getBoolean("isGuest", false)

        initViews()
        setupListener()
        setupRecycler()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupListener() {
        btnBack.setOnClickListener { finish() }
    }

    private fun setupRecycler() {
        rvHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun loadHistory() {

        if (isGuest || auth.currentUser == null) {
            historyList.clear()
            rvHistory.adapter = FileHistoryAdapter(historyList, {}, {})
            return
        }

        val uid = auth.currentUser!!.uid

        historyList.clear()

        database.child("History")
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (data in snapshot.children) {

                        val fileName = data.child("fileName")
                            .getValue(String::class.java) ?: "Materi"

                        val date = data.child("date")
                            .getValue(String::class.java) ?: "-"

                        val documentId = data.child("documentId")
                            .getValue(String::class.java) ?: ""

                        if (documentId.isEmpty()) {
                            Log.e("HISTORY", "documentId kosong untuk file: $fileName")
                        }

                        historyList.add(
                            FileHistoryModel(
                                fileName = fileName,
                                date = date,
                                documentId = documentId
                            )
                        )
                    }

                    rvHistory.adapter = FileHistoryAdapter(
                        historyList,
                        onItemClick = { file ->
                            startActivity(
                                Intent(
                                    this@FileHistoryActivity,
                                    FileHistoryDetailActivity::class.java
                                ).apply {
                                    putExtra("FILE_NAME", file.fileName)
                                    putExtra("DOCUMENT_ID", file.documentId)
                                }
                            )
                        },
                        onDelete = { file ->
                            deleteHistory(uid, file)
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FileHistoryActivity, error.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun deleteHistory(uid: String, file: FileHistoryModel) {

        // Hapus dari History
        database.child("History")
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (data in snapshot.children) {

                        val fileName =
                            data.child("fileName")
                                .getValue(String::class.java)

                        val date =
                            data.child("date")
                                .getValue(String::class.java)

                        if (
                            fileName == file.fileName &&
                            date == file.date
                        ) {

                            data.ref.removeValue()
                        }
                    }

                    database.child("UploadedMaterials")
                        .child(uid)
                        .addListenerForSingleValueEvent(
                            object : ValueEventListener {

                                override fun onDataChange(uploadSnapshot: DataSnapshot) {

                                    for (upload in uploadSnapshot.children) {

                                        val uploadFileName =
                                            upload.child("fileName")
                                                .getValue(String::class.java)

                                        if (uploadFileName == file.fileName) {

                                            upload.ref.removeValue()
                                        }
                                    }


                                    database.child("QuizHistory")
                                        .child(uid)
                                        .addListenerForSingleValueEvent(
                                            object : ValueEventListener {

                                                override fun onDataChange(
                                                    quizSnapshot: DataSnapshot
                                                ) {

                                                    for (quiz in quizSnapshot.children) {

                                                        val quizFileName =
                                                            quiz.child("fileName")
                                                                .getValue(String::class.java)

                                                        if (quizFileName == file.fileName) {

                                                            quiz.ref.removeValue()
                                                        }
                                                    }

                                                    Toast.makeText(
                                                        this@FileHistoryActivity,
                                                        "File dan progress berhasil dihapus",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    loadHistory()
                                                }

                                                override fun onCancelled(
                                                    error: DatabaseError
                                                ) {
                                                }
                                            })
                                }

                                override fun onCancelled(
                                    error: DatabaseError
                                ) {
                                }
                            })
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(
                        this@FileHistoryActivity,
                        error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}