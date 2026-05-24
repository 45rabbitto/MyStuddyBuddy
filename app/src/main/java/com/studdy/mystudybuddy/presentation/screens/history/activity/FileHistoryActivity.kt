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

        database.child("History") // ✔ sudah disamakan dengan UploadActivity
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (data in snapshot.children) {

                        val fileName = data.child("fileName")
                            .getValue(String::class.java) ?: "Materi"

                        val date = data.child("date")
                            .getValue(String::class.java) ?: "-"

                        historyList.add(FileHistoryModel(fileName, date))
                    }

                    rvHistory.adapter = FileHistoryAdapter(
                        historyList,
                        onItemClick = { file ->
                            startActivity(
                                Intent(this@FileHistoryActivity,
                                    FileHistoryDetailActivity::class.java).apply {
                                    putExtra("FILE_NAME", file.fileName)
                                }
                            )
                        },
                        onDelete = { file ->
                            deleteHistory(uid, file)
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FileHistoryActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteHistory(uid: String, file: FileHistoryModel) {

        database.child("History")
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (data in snapshot.children) {

                        val fileName = data.child("fileName").getValue(String::class.java)
                        val date = data.child("date").getValue(String::class.java)

                        if (fileName == file.fileName && date == file.date) {
                            data.ref.removeValue()
                        }
                    }

                    Toast.makeText(this@FileHistoryActivity, "History dihapus", Toast.LENGTH_SHORT).show()
                    loadHistory()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FileHistoryActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}