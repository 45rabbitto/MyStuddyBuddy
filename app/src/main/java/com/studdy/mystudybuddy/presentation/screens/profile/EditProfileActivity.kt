package com.yourpackage.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yourpackage.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var btnChangePhoto: Button

    private lateinit var userPref: android.content.SharedPreferences
    private var imageUri: Uri? = null

    // launcher pilih gambar galeri
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imageUri = it
                imgProfile.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        userPref = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)

        imgProfile = findViewById(R.id.imgProfileEdit)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)

        loadData()

        btnChangePhoto.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        btnSave.setOnClickListener {
            saveProfile()
            finish()
        }
    }

    private fun loadData() {
        etName.setText(userPref.getString("name", ""))
        etEmail.setText(userPref.getString("email", ""))

        val savedUri = userPref.getString("photo_uri", null)
        if (savedUri != null) {
            imgProfile.setImageURI(Uri.parse(savedUri))
        }
    }

    private fun saveProfile() {
        val editor = userPref.edit()
        editor.putString("name", etName.text.toString())
        editor.putString("email", etEmail.text.toString())

        imageUri?.let {
            editor.putString("photo_uri", it.toString())
        }

        editor.apply()
    }
}