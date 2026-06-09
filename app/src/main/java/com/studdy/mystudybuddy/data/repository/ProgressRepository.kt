package com.studdy.mystudybuddy.data.repository

import android.content.Context
import android.content.SharedPreferences

class ProgressRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("progress_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_UPLOAD = "upload_count"
        private const val KEY_SUMMARY = "summary_count"
        private const val KEY_QUIZ = "quiz_count"
        private const val KEY_CHATBOT = "chatbot_count"
    }

    // ================= GET DATA =================
    fun getUploadCount() = prefs.getInt(KEY_UPLOAD, 0)
    fun getSummaryCount() = prefs.getInt(KEY_SUMMARY, 0)
    fun getQuizCount() = prefs.getInt(KEY_QUIZ, 0)
    fun getChatbotCount() = prefs.getInt(KEY_CHATBOT, 0)

    // ================= SAVE DATA =================
    fun saveUploadCount(value: Int) =
        prefs.edit().putInt(KEY_UPLOAD, value).apply()

    fun saveSummaryCount(value: Int) =
        prefs.edit().putInt(KEY_SUMMARY, value).apply()

    fun saveQuizCount(value: Int) =
        prefs.edit().putInt(KEY_QUIZ, value).apply()

    fun saveChatbotCount(value: Int) =
        prefs.edit().putInt(KEY_CHATBOT, value).apply()

    // ================= INCREMENT =================
    fun incrementUpload() = saveUploadCount(getUploadCount() + 1)
    fun incrementSummary() = saveSummaryCount(getSummaryCount() + 1)
    fun incrementQuiz() = saveQuizCount(getQuizCount() + 1)
    fun incrementChatbot() = saveChatbotCount(getChatbotCount() + 1)
}