package com.studdy.mystudybuddy.presentation.screens.auth

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.auth.activity.LoginActivity

class RegisterScreen(
    private val activity: Activity
) {

    private val etUsername: EditText =
        activity.findViewById(R.id.etUsername)

    private val etEmail: EditText =
        activity.findViewById(R.id.etEmail)

    private val etPassword: EditText =
        activity.findViewById(R.id.etPassword)

    private val btnRegister: Button =
        activity.findViewById(R.id.btnRegister)

    fun setupUI() {

        btnRegister.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {

                username.isEmpty() -> {
                    etUsername.error = "Username wajib diisi"
                    return@setOnClickListener
                }

                email.isEmpty() -> {
                    etEmail.error = "Email wajib diisi"
                    return@setOnClickListener
                }

                password.isEmpty() -> {
                    etPassword.error = "Password wajib diisi"
                    return@setOnClickListener
                }


            }

            Toast.makeText(
                activity,
                "Registrasi berhasil",
                Toast.LENGTH_SHORT
            ).show()

            activity.startActivity(
                Intent(
                    activity,
                    LoginActivity::class.java
                )
            )

            activity.finish()
        }
    }
}