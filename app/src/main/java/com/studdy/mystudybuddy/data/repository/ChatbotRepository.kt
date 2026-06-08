package com.studdy.mystudybuddy.data.repository

import com.studdy.mystudybuddy.data.models.ChatMessageModel
import com.studdy.mystudybuddy.data.models.SummaryModel
import com.studdy.mystudybuddy.data.remote.firestore.SummaryFirestoreService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatbotRepository @Inject constructor(
    private val firestore: SummaryFirestoreService,
    private val deepSeekApi: DeepSeekApiService
) {

    /**
     * Ambil semua ringkasan user untuk ditampilkan di daftar pilih materi
     */
    suspend fun getUserSummaries(userId: String): List<SummaryModel> {
        return firestore.getAllSummaries(userId)
    }

    /**
     * Ambil satu ringkasan berdasarkan ID (termasuk summary-nya untuk konteks)
     */
    suspend fun getSummaryById(summaryId: String): SummaryModel? {
        return firestore.getSummaryById(summaryId)
    }

    /**
     * Kirim pertanyaan ke AI dengan konteks dari RINGKASAN (bukan file asli)
     */
    suspend fun chatWithSummaryContext(
        summaryId: String,
        question: String
    ): Result<String> {
        return try {
            // Step 1: Ambil ringkasan dari Firestore (HANYA RINGKASAN, BUKAN FILE ASLI)
            val summary = firestore.getSummaryById(summaryId)
            if (summary == null) {
                return Result.failure(Exception("Ringkasan tidak ditemukan"))
            }

            // Step 2: Ambil riwayat chat terakhir (10 pesan untuk konteks percakapan)
            val chatHistory = firestore.getChatHistory(summaryId, limit = 10)

            // Step 3: Kirim ke DeepSeek API dengan kontkes RINGKASAN
            val answer = deepSeekApi.chatWithContext(
                question = question,
                context = summary.summary,  // ← HANYA RINGKASAN, BUKAN TEKS ASLI!
                chatHistory = chatHistory.map {
                    mapOf("question" to it.question, "answer" to it.answer)
                }
            )

            // Step 4: Simpan pesan ke Firestore
            val chatMessage = ChatMessageModel(
                summaryId = summaryId,
                question = question,
                answer = answer,
                isFromUser = true
            )
            firestore.saveChatMessage(chatMessage)

            Result.success(answer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ambil riwayat chat untuk ditampilkan di UI
     */
    suspend fun getChatHistory(summaryId: String): List<ChatMessageModel> {
        return firestore.getChatHistory(summaryId)
    }
}