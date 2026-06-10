package com.studdy.mystudybuddy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.data.model.DocumentModel
import com.studdy.mystudybuddy.data.model.SummaryModel
import com.studdy.mystudybuddy.network.RetrofitClient
import com.studdy.mystudybuddy.network.SummarizeRequest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class SummaryRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private val COLLECTION_DOCUMENTS = "documents"
    private val COLLECTION_SUMMARIES = "summaries"

    suspend fun saveDocument(
        fileName: String,
        extractedText: String,
        userId: String
    ): String {
        return withContext(Dispatchers.IO) {
            val document = DocumentModel(
                fileName = fileName,
                extractedText = extractedText,
                userId = userId,
                uploadedAt = Date(),
                isProcessed = false
            )
            val docRef = firestore.collection(COLLECTION_DOCUMENTS).document()
            docRef.set(document).await()
            return@withContext docRef.id
        }
    }

    suspend fun getDocumentById(documentId: String): DocumentModel? {
        return withContext(Dispatchers.IO) {
            val doc = firestore.collection(COLLECTION_DOCUMENTS)
                .document(documentId)
                .get()
                .await()
            doc.toObject(DocumentModel::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun processAndSaveSummary(documentId: String): Result<SummaryModel> {
        return withContext(Dispatchers.IO) {
            try {
                val document = getDocumentById(documentId)
                if (document == null) {
                    return@withContext Result.failure(Exception("Dokumen tidak ditemukan"))
                }

                val extractedText = document.extractedText
                if (extractedText.isEmpty()) {
                    return@withContext Result.failure(Exception("Teks kosong"))
                }

                val apiService = RetrofitClient.api
                val response = apiService.summarizeText(SummarizeRequest(extractedText))

                if (!response.isSuccessful || response.body() == null) {
                    return@withContext Result.failure(Exception("Gagal memanggil API: ${response.code()}"))
                }

                val summaryResponse = response.body()!!
                val summaryText = summaryResponse.summary

                val summaryModel = SummaryModel(
                    documentId = documentId,
                    fileName = document.fileName,
                    summary = summaryText,
                    summaryLength = summaryText.length,
                    originalLength = extractedText.length,
                    createdAt = Date(),
                    userId = document.userId
                )

                val summaryRef = firestore.collection(COLLECTION_SUMMARIES).document()
                summaryRef.set(summaryModel).await()
                val savedSummary = summaryModel.copy(id = summaryRef.id)

                val updates: MutableMap<String, Any> = hashMapOf(
                    "isProcessed" to true,
                    "summaryId" to summaryRef.id
                )
                firestore.collection(COLLECTION_DOCUMENTS)
                    .document(documentId)
                    .update(updates)
                    .await()

                return@withContext Result.success(savedSummary)

            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }
    }

    suspend fun getSummaryById(summaryId: String): SummaryModel? {
        return withContext(Dispatchers.IO) {
            val doc = firestore.collection(COLLECTION_SUMMARIES)
                .document(summaryId)
                .get()
                .await()
            doc.toObject(SummaryModel::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun getAllSummaries(userId: String): List<SummaryModel> {
        return withContext(Dispatchers.IO) {
            val snapshot = firestore.collection(COLLECTION_SUMMARIES)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(SummaryModel::class.java)?.copy(id = it.id) }
        }
    }
}