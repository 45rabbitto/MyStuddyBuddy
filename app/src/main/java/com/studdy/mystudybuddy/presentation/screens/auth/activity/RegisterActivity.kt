package com.studdy.mystudybuddy.presentation.screens.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.btnLogin)
    }

    private fun setupClickListeners() {

        btnRegister.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                username.isEmpty() -> {
                    etUsername.error = "Username wajib diisi"
                    etUsername.requestFocus()
                }

                email.isEmpty() -> {
                    etEmail.error = "Email wajib diisi"
                    etEmail.requestFocus()
                }

                password.isEmpty() -> {
                    etPassword.error = "Password wajib diisi"
                    etPassword.requestFocus()
                }

                password.length < 6 -> {
                    etPassword.error = "Password minimal 6 karakter"
                    etPassword.requestFocus()
                }

                else -> {

                    Toast.makeText(
                        this,
                        "Registrasi berhasil",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )

                    finish()
                }
            }
        }

        tvLogin.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }
}