package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.studdy.mystudybuddy.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    // Image
    private var imageUri: Uri? = null
    private var currentImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        // Firebase init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        initViews()
        setupListeners()
        loadProfile()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        imgProfile =
            findViewById(R.id.imgProfile)

        etName =
            findViewById(R.id.etName)

        etEmail =
            findViewById(R.id.etEmail)

        btnSave =
            findViewById(R.id.btnSave)
    }

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

                        if (snapshot.exists()) {

                            val username =
                                snapshot.child("username")
                                    .getValue(String::class.java)
                                    ?: ""

                            val email =
                                snapshot.child("email")
                                    .getValue(String::class.java)
                                    ?: ""

                            val profileImage =
                                snapshot.child("profileImage")
                                    .getValue(String::class.java)
                                    ?: ""

                            etName.setText(username)
                            etEmail.setText(email)

                            currentImageUrl =
                                profileImage

                            if (profileImage.isNotEmpty()) {

                                Glide.with(this@EditProfileActivity)
                                    .load(profileImage)
                                    .placeholder(R.drawable.profil)
                                    .into(imgProfile)
                            }
                        }
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Toast.makeText(
                            this@EditProfileActivity,
                            error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        // pilih gambar
        imgProfile.setOnClickListener {

            pickImage()
        }

        // save profile
        btnSave.setOnClickListener {

            val name =
                etName.text.toString().trim()

            val email =
                etEmail.text.toString().trim()

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

            if (imageUri != null) {

                uploadImageAndSaveData(
                    name,
                    email
                )

            } else {

                saveProfileData(
                    name,
                    email,
                    currentImageUrl
                )
            }
        }
    }

    // pilih gambar dari galeri
    private fun pickImage() {

        val intent =
            android.content.Intent().apply {

                type = "image/*"

                action =
                    android.content.Intent.ACTION_GET_CONTENT
            }

        launcher.launch(intent)
    }

    // launcher image picker
    private val launcher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                imageUri =
                    result.data?.data

                imgProfile.setImageURI(
                    imageUri
                )
            }
        }

    // upload image ke firebase storage
    private fun uploadImageAndSaveData(
        name: String,
        email: String
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        val ref =
            storage.reference
                .child("profile_images")
                .child("$uid.jpg")

        imageUri?.let { uri ->

            ref.putFile(uri)

                .addOnSuccessListener {

                    ref.downloadUrl
                        .addOnSuccessListener { downloadUrl ->

                            saveProfileData(
                                name,
                                email,
                                downloadUrl.toString()
                            )
                        }
                }

                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Upload gambar gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    // simpan data ke realtime database
    private fun saveProfileData(
        name: String,
        email: String,
        imageUrl: String
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        val userMap =
            HashMap<String, Any>()

        userMap["username"] =
            name

        userMap["email"] =
            email

        userMap["profileImage"] =
            imageUrl

        database.child("Users")
            .child(uid)
            .updateChildren(userMap)

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Profil berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal update profil",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}