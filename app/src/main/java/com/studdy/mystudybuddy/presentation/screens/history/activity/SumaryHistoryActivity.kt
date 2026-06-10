package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R

class SummaryHistoryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var tvSummary: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_history_summary
        )

        // Firebase init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        initViews()
        loadData()
        setupListener()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        tvFileName =
            findViewById(R.id.tvFileName)

        tvSummary =
            findViewById(R.id.tvSummary)
    }

    private fun loadData() {

        val fileName =
            intent.getStringExtra("FILE_NAME")
                ?: "Dokumen"

        tvFileName.text =
            fileName

        val uid =
            auth.currentUser?.uid ?: return

        database.child("summary_history")
            .child(uid)
            .child(fileName)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (snapshot.exists()) {

                            val summary =
                                snapshot.child("summary")
                                    .getValue(String::class.java)
                                    ?: "Ringkasan kosong"

                            tvSummary.text =
                                summary

                        } else {

                            tvSummary.text =
                                """
                                Ringkasan belum tersedia
                                
                                Silakan generate ringkasan terlebih dahulu.
                                """.trimIndent()
                        }
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        tvSummary.text =
                            "Gagal mengambil data"
                    }
                }
            )
    }

    private fun setupListener() {

        btnBack.setOnClickListener {
            finish()
        }
    }
}