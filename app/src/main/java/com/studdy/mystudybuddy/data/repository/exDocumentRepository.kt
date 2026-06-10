package com.studdy.mystudybuddy.data.repository

import android.content.Context
import android.net.Uri
import com.studdy.mystudybuddy.data.model.DocumentModel
import com.studdy.mystudybuddy.data.remote.api.RetrofitClient
import com.studdy.mystudybuddy.data.remote.api.SummarizeRequest
import com.studdy.mystudybuddy.utils.PDFUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class exDocumentRepository @Inject constructor() {

    suspend fun uploadAndSummarizeDocument(
        context: Context,
        uri: Uri,
        fileName: String,
        userId: String
    ): Result<DocumentModel> = withContext(Dispatchers.IO) {
        try {
            val extractedText = PDFUtils.extractTextFromPdf(context, uri)

            if (extractedText.isBlank() || extractedText.length < 50) {
                return@withContext Result.failure(Exception("Gagal atau teks terlalu pendek"))
            }

            val apiService = RetrofitClient.instance
            val response = apiService.summarizeText(SummarizeRequest(extractedText))

            if (!response.success) {
                return@withContext Result.failure(Exception("Gagal membuat ringkasan"))
            }

            val document = DocumentModel(
                fileName = fileName,
                fileUri = uri.toString(),
                extractedText = extractedText,
                summary = response.summary,
                userId = userId,
                uploadedAt = Date(),
                isProcessed = true
            )

            return@withContext Result.success(document)

        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun getAllDocuments(userId: String): List<DocumentModel> {
        return emptyList()
    }

    suspend fun checkBackendHealth(): Boolean {
        return try {
            val response = RetrofitClient.instance.healthCheck()
            response.status == "ok"
        } catch (e: Exception) {
            false
        }
    }
}