package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var isGuest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        database =
            FirebaseDatabase
                .getInstance()
                .reference

        val session =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        isGuest =
            session.getBoolean(
                "isGuest",
                false
            )

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

    // =========================================
    // LOAD PROFILE
    // =========================================

    private fun loadProfile() {

        // =========================
        // GUEST MODE
        // =========================

        if (
            isGuest ||
            auth.currentUser == null
        ) {

            tvUsername.text = "Guest"

            tvEmail.text = "-"

            imgProfile.setImageResource(
                R.drawable.ava_cewe
            )

            return
        }

        val uid =
            auth.currentUser!!.uid

        database.child("Users")
            .child(uid)
            .addListenerForSingleValueEvent(

                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        val username =
                            snapshot.child("username")
                                .getValue(String::class.java)
                                ?: "User"

                        val email =
                            snapshot.child("email")
                                .getValue(String::class.java)
                                ?: "-"

                        val avatarName =
                            snapshot.child("profileImage")
                                .getValue(String::class.java)
                                ?: "ava_cewe"

                        tvUsername.text =
                            username

                        tvEmail.text =
                            email

                        // =========================
                        // LOAD AVATAR DRAWABLE
                        // =========================

                        val imageRes =
                            resources.getIdentifier(
                                avatarName,
                                "drawable",
                                packageName
                            )

                        if (imageRes != 0) {

                            imgProfile.setImageResource(
                                imageRes
                            )

                        } else {

                            imgProfile.setImageResource(
                                R.drawable.ava_cewe
                            )
                        }
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Toast.makeText(
                            this@ProfileActivity,
                            "Gagal memuat profil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    // =========================================
    // LISTENER
    // =========================================

    private fun setupListeners() {

        // BACK
        btnBack.setOnClickListener {

            finish()
        }

        // EDIT PROFILE
        btnEditProfile.setOnClickListener {

            if (isGuest) {

                Toast.makeText(
                    this,
                    "Login untuk edit profil",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            startActivity(
                Intent(
                    this,
                    EditProfileActivity::class.java
                )
            )
        }

        // LOGOUT
        menuLogout.setOnClickListener {

            auth.signOut()

            val session =
                getSharedPreferences(
                    "user_session",
                    MODE_PRIVATE
                )

            session.edit()
                .putBoolean(
                    "isGuest",
                    true
                )
                .apply()

            val intent =
                Intent(
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