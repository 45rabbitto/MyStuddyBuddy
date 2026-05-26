package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.app.Activity
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.studdy.mystudybuddy.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgProfileEdit: ImageView
    private lateinit var btnChangePhoto: Button

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSave: Button

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private var imageUri: Uri? = null
    private var currentImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()

        database =
            FirebaseDatabase
                .getInstance()
                .reference

        storage =
            FirebaseStorage.getInstance()

        storageRef =
            storage.reference

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
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        btnChangePhoto.setOnClickListener {
            pickImage()
        }

        imgProfileEdit.setOnClickListener {
            pickImage()
        }

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

            // kalau user pilih foto baru
            if (imageUri != null) {

                uploadImageAndSave(
                    name,
                    email,
                    password
                )

            } else {

                saveProfileData(
                    name,
                    email,
                    password,
                    currentImageUrl
                )
            }
        }
    }

    // =========================================
    // LOAD PROFILE
    // =========================================

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

                        currentImageUrl =
                            snapshot.child("profileImage")
                                .getValue(String::class.java)
                                ?: ""

                        if (
                            currentImageUrl.isNotEmpty()
                        ) {

                            Glide.with(
                                this@EditProfileActivity
                            )
                                .load(currentImageUrl)
                                .placeholder(R.drawable.profil)
                                .into(imgProfileEdit)
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

    // =========================================
    // PILIH FOTO
    // =========================================

    private fun pickImage() {

        val intent =
            Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "image/*"

        imageLauncher.launch(intent)
    }

    private val imageLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (
                result.resultCode == Activity.RESULT_OK
            ) {

                imageUri =
                    result.data?.data

                imgProfileEdit.setImageURI(
                    imageUri
                )
            }
        }

    // =========================================
    // UPLOAD FOTO
    // =========================================

    private fun uploadImageAndSave(
        name: String,
        email: String,
        password: String
    ) {

        val uid =
            auth.currentUser?.uid

        if (uid == null) {

            Toast.makeText(
                this,
                "User tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()

            btnSave.isEnabled = true
            return
        }

        val uri = imageUri

        if (uri == null) {

            Toast.makeText(
                this,
                "Pilih gambar terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()

            btnSave.isEnabled = true
            return
        }

        val imageRef =
            storageRef.child(
                "profile_images/$uid.jpg"
            )

        Log.d(
            "UPLOAD",
            "Mulai upload..."
        )

        imageRef.putFile(uri)

            .addOnSuccessListener {

                Log.d(
                    "UPLOAD",
                    "Upload berhasil"
                )

                imageRef.downloadUrl

                    .addOnSuccessListener { downloadUri ->

                        val imageUrl =
                            downloadUri.toString()

                        Log.d(
                            "UPLOAD",
                            imageUrl
                        )

                        saveProfileData(
                            name,
                            email,
                            password,
                            imageUrl
                        )
                    }

                    .addOnFailureListener { e ->

                        btnSave.isEnabled = true

                        Toast.makeText(
                            this,
                            "Gagal mengambil URL",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.e(
                            "UPLOAD",
                            "DOWNLOAD URL ERROR",
                            e
                        )
                    }
            }

            .addOnFailureListener { e ->

                btnSave.isEnabled = true

                Toast.makeText(
                    this,
                    "Upload gagal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                Log.e(
                    "UPLOAD",
                    "UPLOAD ERROR",
                    e
                )
            }
    }

    // =========================================
    // SAVE DATA PROFILE
    // =========================================

    private fun saveProfileData(
        name: String,
        email: String,
        password: String,
        imageUrl: String
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        val userMap =
            hashMapOf<String, Any>(
                "username" to name,
                "email" to email,
                "profileImage" to imageUrl
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

    // =========================================
    // CHECK INTERNET
    // =========================================

    private fun isInternetAvailable(): Boolean {

        val cm =
            getSystemService(
                CONNECTIVITY_SERVICE
            ) as ConnectivityManager

        return cm.activeNetworkInfo?.isConnected == true
    }
}