package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R

class AlurFileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var fileContainer: LinearLayout
    private lateinit var tvKosong: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_alur_file
        )

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        initViews()

        loadFiles()

        setupListeners()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        fileContainer =
            findViewById(R.id.fileContainer)

        tvKosong =
            findViewById(R.id.tvKosong)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {

            finish()
        }
    }

    private fun loadFiles() {

        val uid =
            auth.currentUser?.uid ?: return

        database.child("uploads")
            .child(uid)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        fileContainer.removeAllViews()

                        if (!snapshot.exists()) {

                            tvKosong.visibility =
                                View.VISIBLE

                            return
                        }

                        tvKosong.visibility =
                            View.GONE

                        for (data in snapshot.children) {

                            val fileName =
                                data.child("fileName")
                                    .getValue(String::class.java)
                                    ?: "Dokumen"

                            val itemView =
                                layoutInflater.inflate(
                                    R.layout.item_alur_file,
                                    fileContainer,
                                    false
                                )

                            val tvNamaFile =
                                itemView.findViewById<TextView>(
                                    R.id.tvNamaFile
                                )

                            tvNamaFile.text =
                                fileName

                            itemView.setOnClickListener {

                                val intent = Intent(
                                    this@AlurFileActivity,
                                    AlurActivity::class.java
                                )

                                intent.putExtra(
                                    "FILE_NAME",
                                    fileName
                                )

                                startActivity(intent)
                            }

                            fileContainer.addView(
                                itemView
                            )
                        }
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        tvKosong.visibility =
                            View.VISIBLE

                        tvKosong.text =
                            "Gagal mengambil data"
                    }
                }
            )
    }
}