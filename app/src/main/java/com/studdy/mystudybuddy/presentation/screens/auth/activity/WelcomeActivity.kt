package com.studdy.mystudybuddy.presentation.screens.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
    }

    private fun setupClickListeners() {

        btnLogin.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
        }

        btnRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }
}