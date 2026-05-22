package com.studdy.mystudybuddy.presentation.screens.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.main.MainActivity
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()

        initViews()
        setupClickListeners()
    }

    private fun initViews() {

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
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

                    // Register Firebase Auth
                    auth.createUserWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {

                            val userId = auth.currentUser?.uid

                            // Data user
                            val userMap = HashMap<String, Any>()
                            userMap["username"] = username
                            userMap["email"] = email

                            // Simpan ke Realtime Database
                            if (userId != null) {

                                FirebaseDatabase
                                    .getInstance()
                                    .getReference("Users")
                                    .child(userId)
                                    .setValue(userMap)
                                    .addOnSuccessListener {

                                        Toast.makeText(
                                            this,
                                            "Registrasi berhasil",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // PINDAH KE MAIN ACTIVITY
                                        startActivity(
                                            Intent(
                                                this,
                                                DashboardActivity::class.java
                                            )
                                        )

                                        finish()
                                    }

                                    .addOnFailureListener {

                                        Toast.makeText(
                                            this,
                                            "Gagal menyimpan data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }

                        } else {

                            Toast.makeText(
                                this,
                                "Register gagal: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
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