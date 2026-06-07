package com.studdy.mystudybuddy.data.repository

import android.content.Context
import android.net.Uri
import com.studdy.mystudybuddy.data.models.DocumentModel
import com.studdy.mystudybuddy.data.remote.api.RetrofitClient
import com.studdy.mystudybuddy.data.remote.api.SummarizeRequest
import com.studdy.mystudybuddy.data.remote.firestore.FirestoreService
import com.studdy.mystudybuddy.utils.PDFUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    /**
     * ALUR LENGKAP:
     * 1. Ekstrak teks dari PDF (PDFBox)
     * 2. Kirim teks ke backend Python (MobileBERT)
     * 3. Terima ringkasan
     * 4. Simpan ke Firestore
     */
    suspend fun uploadAndSummarizeDocument(
        context: Context,
        uri: Uri,
        fileName: String,
        userId: String
    ): Result<DocumentModel> = withContext(Dispatchers.IO) {
        try {
            // STEP 1: Ekstrak teks dari PDF pakai PDFBox
            val extractedText = PDFUtils.extractTextFromPdf(context, uri)

            if (extractedText.isBlank()) {
                return@withContext Result.failure(Exception("Gagal mengekstrak teks dari PDF"))
            }

            if (extractedText.length < 50) {
                return@withContext Result.failure(Exception("Teks terlalu pendek (minimal 50 karakter)"))
            }

            // STEP 2: Kirim teks ke backend Python (model lokal MobileBERT)
            val apiService = RetrofitClient.instance
            val response = apiService.summarizeText(SummarizeRequest(extractedText))

            if (!response.success) {
                return@withContext Result.failure(Exception("Gagal membuat ringkasan"))
            }

            // STEP 3: Buat model dokumen
            val document = DocumentModel(
                fileName = fileName,
                fileUri = uri.toString(),
                extractedText = extractedText,
                summary = response.summary,
                summaryLength = response.summary_length,
                userId = userId,
                uploadedAt = Date(),
                isProcessed = true
            )

            // STEP 4: Simpan ke Firestore
            val documentId = firestoreService.saveDocument(document)
            val savedDocument = document.copy(id = documentId)

            return@withContext Result.success(savedDocument)

        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Ambil semua dokumen user
     */
    suspend fun getAllDocuments(userId: String): List<DocumentModel> {
        return firestoreService.getAllDocuments(userId)
    }

    /**
     * Cek kesehatan backend
     */
    suspend fun checkBackendHealth(): Boolean {
        return try {
            val response = RetrofitClient.instance.healthCheck()
            response.status == "ok" && response.model_loaded
        } catch (e: Exception) {
            false
        }
    }
}