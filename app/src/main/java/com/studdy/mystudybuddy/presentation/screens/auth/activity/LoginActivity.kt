package com.studdy.mystudybuddy.presentation.screens.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // AUTO LOGIN
        if (auth.currentUser != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        database = FirebaseDatabase.getInstance().reference

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
    }

    private fun setupClickListeners() {

        btnLogin.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Username wajib diisi"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                        database.child("Users")
                            .child(uid)
                            .addListenerForSingleValueEvent(
                                object : ValueEventListener {

                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        val dbUsername =
                                            snapshot.child("username")
                                                .getValue(String::class.java)

                                        if (dbUsername == username) {

                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Login berhasil",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            startActivity(
                                                Intent(
                                                    this@LoginActivity,
                                                    DashboardActivity::class.java
                                                )
                                            )

                                            finish()

                                        } else {

                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Username salah",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            auth.signOut()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            error.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )

                    } else {
                        Toast.makeText(
                            this,
                            "Login gagal: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}