package com.studdy.mystudybuddy.presentation.screens.auth.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleSignIn: LinearLayout
    private lateinit var tvRegister: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // AUTO LOGIN
        if (auth.currentUser != null) {
            navigateToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        database = FirebaseDatabase.getInstance().reference

        initViews()
        setupGoogleSignIn()
        setupClickListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        tvRegister = findViewById(R.id.tvRegister)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
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
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val dbUsername = snapshot.child("username").getValue(String::class.java)

                                    if (dbUsername == username) {
                                        Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                                        navigateToDashboard()
                                    } else {
                                        Toast.makeText(this@LoginActivity, "Username salah", Toast.LENGTH_SHORT).show()
                                        auth.signOut()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: com.google.android.gms.auth.api.signin.GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                val email = authResult.user?.email ?: ""
                val displayName = authResult.user?.displayName ?: ""

                database.child("Users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            val username = if (displayName.isNotEmpty()) {
                                displayName.replace(" ", "_").lowercase()
                            } else {
                                email.substringBefore("@")
                            }

                            val userMap = hashMapOf(
                                "uid" to uid,
                                "username" to username,
                                "email" to email,
                                "displayName" to displayName,
                                "createdAt" to System.currentTimeMillis()
                            )

                            database.child("Users").child(uid).setValue(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this@LoginActivity, " Akun Google berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                    //  TAMBAHKAN DELAY AGAR DATA TERSIMPAN DULU
                                    navigateToDashboardWithDelay()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@LoginActivity, "Gagal menyimpan data: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this@LoginActivity, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()
                            navigateToDashboardWithDelay()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firebase Auth Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    //  NAVIGASI DENGAN FLAG CLEAR TASK
    private fun navigateToDashboard() {
        //  SET isGuest = false AGAR TIDAK DIANGGAP GUEST
        val session = getSharedPreferences("user_session", MODE_PRIVATE)
        session.edit().putBoolean("isGuest", false).apply()

        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    //  NAVIGASI DENGAN DELAY (UNTUK GOOGLE LOGIN)
    private fun navigateToDashboardWithDelay() {
        // Tunggu 500ms agar Firebase Auth state benar-benar terupdate
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToDashboard()
        }, 500)
    }
}