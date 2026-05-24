package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.app.Activity
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.studdy.mystudybuddy.R
import java.util.UUID

class EditProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgProfileEdit: ImageView
    private lateinit var btnChangePhoto: Button

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSave: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private var imageUri: Uri? = null
    private var currentImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        initViews()
        setupListeners()
        loadProfile()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        imgProfileEdit = findViewById(R.id.imgProfileEdit)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnSave = findViewById(R.id.btnSave)
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
                Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Nama wajib diisi"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            btnSave.isEnabled = false

            if (imageUri != null) {
                uploadImageAndSave(name, email, password)
            } else {
                saveProfileData(name, email, password, currentImageUrl)
            }
        }
    }

    private fun loadProfile() {

        val uid = auth.currentUser?.uid ?: return

        database.child("Users")
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val username = snapshot.child("username").getValue(String::class.java) ?: ""
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val profileImage = snapshot.child("profileImage").getValue(String::class.java) ?: ""

                    etName.setText(username)
                    etEmail.setText(email)

                    currentImageUrl = profileImage

                    if (profileImage.isNotEmpty()) {
                        Glide.with(this@EditProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.profil)
                            .into(imgProfileEdit)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditProfileActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageLauncher.launch(intent)
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                imageUri = result.data?.data

                if (imageUri == null) {
                    Toast.makeText(this, "Gagal memilih gambar", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                imgProfileEdit.setImageURI(imageUri)
            }
        }

    private fun uploadImageAndSave(name: String, email: String, password: String) {

        val uid = auth.currentUser?.uid ?: return

        val uri = imageUri
        if (uri == null) {
            btnSave.isEnabled = true
            Toast.makeText(this, "Image tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = UUID.randomUUID().toString() + ".jpg"

        val storageRef = storage.reference
            .child("profile_images/$fileName")

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->

                val userMap = hashMapOf<String, Any>(
                    "username" to name,
                    "email" to email,
                    "profileImage" to downloadUri.toString()
                )

                database.child("Users")
                    .child(uid)
                    .setValue(userMap)
                    .addOnSuccessListener {

                        auth.currentUser?.updateEmail(email)

                        if (password.isNotEmpty()) {
                            auth.currentUser?.updatePassword(password)
                        }

                        btnSave.isEnabled = true

                        Toast.makeText(
                            this,
                            "Profil berhasil diperbarui",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
            }
            .addOnFailureListener { e ->
                btnSave.isEnabled = true
                Toast.makeText(this, "Upload gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveProfileData(
        name: String,
        email: String,
        password: String,
        imageUrl: String
    ) {

        val uid = auth.currentUser?.uid ?: return

        val userMap = hashMapOf<String, Any>(
            "username" to name,
            "email" to email,
            "profileImage" to imageUrl
        )

        auth.currentUser?.updateEmail(email)
            ?.addOnCompleteListener {

                database.child("Users")
                    .child(uid)
                    .setValue(userMap)
                    .addOnSuccessListener {

                        if (password.isNotEmpty()) {
                            auth.currentUser?.updatePassword(password)
                        }

                        btnSave.isEnabled = true

                        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {

                        btnSave.isEnabled = true

                        Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetworkInfo
        return network != null && network.isConnected
    }
}