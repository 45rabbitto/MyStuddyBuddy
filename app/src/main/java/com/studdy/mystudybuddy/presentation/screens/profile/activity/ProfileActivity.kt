package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.auth.activity.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnEditProfile: LinearLayout
    private lateinit var btnBack: ImageView
    private lateinit var menuLogout: LinearLayout

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var imgProfile: ImageView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        initViews()
        loadProfile()
        setupListeners()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        btnEditProfile =
            findViewById(R.id.btnEditProfile)

        menuLogout =
            findViewById(R.id.menuLogout)

        tvUsername =
            findViewById(R.id.tvName)

        tvEmail =
            findViewById(R.id.tvEmail)

        imgProfile =
            findViewById(R.id.imgProfile)
    }

    private fun loadProfile() {

        val uid = auth.currentUser?.uid ?: return

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
                                    ?: "-"

                            val email =
                                snapshot.child("email")
                                    .getValue(String::class.java)
                                    ?: "-"

                            val profileImage =
                                snapshot.child("profileImage")
                                    .getValue(String::class.java)
                                    ?: ""

                            tvUsername.text = username
                            tvEmail.text = email

                            // tampilkan foto profil
                            if (profileImage.isNotEmpty()) {

                                Glide.with(this@ProfileActivity)
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
                            this@ProfileActivity,
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

        btnEditProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EditProfileActivity::class.java
                )
            )
        }

        menuLogout.setOnClickListener {

            auth.signOut()

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}