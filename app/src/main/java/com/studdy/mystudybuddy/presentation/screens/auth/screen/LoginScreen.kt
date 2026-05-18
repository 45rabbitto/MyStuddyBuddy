package com.studdy.mystudybuddy.presentation.screens.auth

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.main.MainActivity
import com.studdy.mystudybuddy.presentation.screens.auth.activity.RegisterActivity

class LoginScreen(
    private val activity: Activity
) {

    private val etEmail: EditText =
        activity.findViewById(R.id.etEmail)

    private val etPassword: EditText =
        activity.findViewById(R.id.etPassword)

    private val btnLogin: Button =
        activity.findViewById(R.id.btnLogin)

    private val tvRegister: TextView =
        activity.findViewById(R.id.tvRegister)

    fun setupUI() {

        btnLogin.setOnClickListener {

            val username = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etEmail.error = "Username wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }

            Toast.makeText(
                activity,
                "Login berhasil",
                Toast.LENGTH_SHORT
            ).show()

            activity.startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                )
            )
        }

        tvRegister.setOnClickListener {

            activity.startActivity(
                Intent(
                    activity,
                    RegisterActivity::class.java
                )
            )
        }
    }
}