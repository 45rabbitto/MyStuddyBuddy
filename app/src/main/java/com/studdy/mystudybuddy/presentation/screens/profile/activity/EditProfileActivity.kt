package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        initViews()
        setupListeners()
        loadProfile()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun loadProfile() {
        etName.setText("Student")
        etEmail.setText("student@email.com")
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Nama wajib diisi"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Profil berhasil diperbarui",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}