package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class AlurFileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var fileContainer: LinearLayout
    private lateinit var tvKosong: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_alur_file
        )

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

        // sementara data dummy
        val fileList = listOf(
            "Biologi.pdf",
            "Kimia Dasar.pdf",
            "Matematika.pdf"
        )

        if (fileList.isEmpty()) {

            tvKosong.visibility =
                android.view.View.VISIBLE

            return
        }

        tvKosong.visibility =
            android.view.View.GONE

        fileList.forEach { fileName ->

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
                    this,
                    AlurActivity::class.java
                )

                intent.putExtra(
                    "MATERI",
                    fileName
                )

                startActivity(intent)
            }

            fileContainer.addView(
                itemView
            )
        }
    }
}