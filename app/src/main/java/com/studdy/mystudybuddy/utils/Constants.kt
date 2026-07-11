package com.studdy.mystudybuddy.utils

import com.google.firebase.auth.FirebaseAuth

object Constants {

    /**
     * Mendapatkan userId dari Firebase Auth.
     * Jika user login → return uid
     * Jika guest → return "guest"
     */
    fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
    }

    /**
     * Cek apakah user adalah guest
     */
    fun isGuest(): Boolean {
        return FirebaseAuth.getInstance().currentUser == null
    }
}