package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.studdy.mystudybuddy.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgProfileEdit: ImageView
    private lateinit var btnChangePhoto: Button

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSave: Button

    // Avatar
    private lateinit var avatarBuku: ImageView
    private lateinit var avatarCewe: ImageView
    private lateinit var avatarCowo: ImageView
    private lateinit var avatarMieAyam: ImageView
    private lateinit var avatarRobot: ImageView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // Avatar terpilih
    private var selectedAvatar = "ava_cewe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()

        database =
            FirebaseDatabase
                .getInstance()
                .reference

        initViews()
        setupListeners()
        loadProfile()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        imgProfileEdit =
            findViewById(R.id.imgProfileEdit)

        btnChangePhoto =
            findViewById(R.id.btnChangePhoto)

        etName =
            findViewById(R.id.etName)

        etEmail =
            findViewById(R.id.etEmail)

        etPassword =
            findViewById(R.id.etPassword)

        btnSave =
            findViewById(R.id.btnSave)

        // Avatar
        avatarBuku =
            findViewById(R.id.avatarBuku)

        avatarCewe =
            findViewById(R.id.avatarCewe)

        avatarCowo =
            findViewById(R.id.avatarCowo)

        avatarMieAyam =
            findViewById(R.id.avatarMieAyam)

        avatarRobot =
            findViewById(R.id.avatarRobot)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        // =========================
        // PILIH AVATAR
        // =========================

        avatarBuku.setOnClickListener {

            selectedAvatar = "ava_buku"

            imgProfileEdit.setImageResource(
                R.drawable.ava_buku
            )
        }

        avatarCewe.setOnClickListener {

            selectedAvatar = "ava_cewe"

            imgProfileEdit.setImageResource(
                R.drawable.ava_cewe
            )
        }

        avatarCowo.setOnClickListener {

            selectedAvatar = "ava_cowo"

            imgProfileEdit.setImageResource(
                R.drawable.ava_cowo
            )
        }

        avatarMieAyam.setOnClickListener {

            selectedAvatar = "ava_miayam"

            imgProfileEdit.setImageResource(
                R.drawable.ava_miayam
            )
        }

        avatarRobot.setOnClickListener {

            selectedAvatar = "ava_robot"

            imgProfileEdit.setImageResource(
                R.drawable.ava_robot
            )
        }

        // =========================
        // SAVE PROFILE
        // =========================

        btnSave.setOnClickListener {

            if (!isInternetAvailable()) {

                Toast.makeText(
                    this,
                    "Tidak ada koneksi internet",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val name =
                etName.text.toString().trim()

            val email =
                etEmail.text.toString().trim()

            val password =
                etPassword.text.toString().trim()

            if (name.isEmpty()) {

                etName.error =
                    "Nama wajib diisi"

                return@setOnClickListener
            }

            if (email.isEmpty()) {

                etEmail.error =
                    "Email wajib diisi"

                return@setOnClickListener
            }

            btnSave.isEnabled = false

            saveProfileData(
                name,
                email,
                password,
                selectedAvatar
            )
        }
    }

    // =========================
    // LOAD PROFILE
    // =========================

    private fun loadProfile() {

        val uid =
            auth.currentUser?.uid ?: return

        database.child("Users")
            .child(uid)
            .addListenerForSingleValueEvent(

                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        etName.setText(
                            snapshot.child("username")
                                .getValue(String::class.java)
                        )

                        etEmail.setText(
                            snapshot.child("email")
                                .getValue(String::class.java)
                        )

                        selectedAvatar =
                            snapshot.child("profileImage")
                                .getValue(String::class.java)
                                ?: "ava_cewe"

                        val avatarRes =
                            resources.getIdentifier(
                                selectedAvatar,
                                "drawable",
                                packageName
                            )

                        if (avatarRes != 0) {

                            imgProfileEdit.setImageResource(
                                avatarRes
                            )
                        }
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Gagal memuat profil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    // =========================
    // SAVE DATA PROFILE
    // =========================

    private fun saveProfileData(
        name: String,
        email: String,
        password: String,
        avatar: String
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        val userMap =
            hashMapOf<String, Any>(

                "username" to name,

                "email" to email,

                "profileImage" to avatar
            )

        database.child("Users")
            .child(uid)
            .updateChildren(userMap)

            .addOnSuccessListener {

                btnSave.isEnabled = true

                Toast.makeText(
                    this,
                    "Profil berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }

            .addOnFailureListener {

                btnSave.isEnabled = true

                Toast.makeText(
                    this,
                    "Gagal menyimpan data",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================
    // CHECK INTERNET
    // =========================

    private fun isInternetAvailable(): Boolean {

        val cm =
            getSystemService(
                CONNECTIVITY_SERVICE
            ) as ConnectivityManager

        return cm.activeNetworkInfo?.isConnected == true
    }
}