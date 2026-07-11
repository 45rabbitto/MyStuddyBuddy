package com.studdy.mystudybuddy.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.data.model.DocumentModel
import com.studdy.mystudybuddy.data.model.SummaryModel
import com.studdy.mystudybuddy.network.RetrofitClient
import com.studdy.mystudybuddy.network.SummarizeRequest
import com.studdy.mystudybuddy.utils.Constants
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class SummaryRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private val COLLECTION_PDF_CONTENTS = "PdfContents"
    private val COLLECTION_DOCUMENTS = "documents"
    private val COLLECTION_SUMMARIES = "summaries"

    /**
     * Simpan dokumen ke Firestore
     * Menggunakan Constants.getUserId() untuk konsistensi
     */
    suspend fun saveDocument(
        fileName: String,
        content: String
    ): String {
        return withContext(Dispatchers.IO) {
            // 🔥 PAKAI Constants.getUserId()
            val userId = Constants.getUserId()

            val document = DocumentModel(
                fileName = fileName,
                content = content,
                userId = userId,
                uploadedAt = Date(),
                isProcessed = false
            )

            val docRef = firestore.collection(COLLECTION_PDF_CONTENTS)
                .document(userId)
                .collection(COLLECTION_DOCUMENTS)
                .document()
            docRef.set(document).await()
            Log.d("SummaryRepo", "Document saved: ${docRef.id} for user: $userId")
            return@withContext docRef.id
        }
    }

    /**
     * Ambil dokumen berdasarkan userId dan documentId
     */
    suspend fun getDocumentById(documentId: String): DocumentModel? {
        return withContext(Dispatchers.IO) {
            // 🔥 PAKAI Constants.getUserId()
            val userId = Constants.getUserId()

            val doc = firestore.collection(COLLECTION_PDF_CONTENTS)
                .document(userId)
                .collection(COLLECTION_DOCUMENTS)
                .document(documentId)
                .get()
                .await()
            doc.toObject(DocumentModel::class.java)?.copy(id = doc.id)
        }
    }

    /**
     * Simpan ringkasan ke Firestore
     */
    suspend fun saveSummary(summaryModel: SummaryModel): String {
        return withContext(Dispatchers.IO) {
            val docRef = firestore.collection(COLLECTION_SUMMARIES).document()
            docRef.set(summaryModel).await()
            Log.d("SummaryRepo", "Summary saved: ${docRef.id}")
            return@withContext docRef.id
        }
    }

    /**
     * Update dokumen dengan summaryId
     */
    suspend fun updateDocumentWithSummary(documentId: String, summaryId: String) {
        withContext(Dispatchers.IO) {
            // 🔥 PAKAI Constants.getUserId()
            val userId = Constants.getUserId()

            firestore.collection(COLLECTION_PDF_CONTENTS)
                .document(userId)
                .collection(COLLECTION_DOCUMENTS)
                .document(documentId)
                .update("isProcessed", true, "summaryId", summaryId)
                .await()
            Log.d("SummaryRepo", "Document updated with summaryId: $summaryId")
        }
    }

    /**
     * Proses ringkasan: ambil teks dari Firestore, kirim ke API, simpan hasil
     */
    suspend fun processAndSaveSummary(documentId: String): Result<SummaryModel> {
        return withContext(Dispatchers.IO) {
            try {
                // 🔥 PAKAI Constants.getUserId()
                val userId = Constants.getUserId()

                // Ambil dokumen dari Firestore
                val document = getDocumentById(documentId)
                if (document == null) {
                    return@withContext Result.failure(Exception("Dokumen tidak ditemukan"))
                }

                val extractedText = document.content
                if (extractedText.isEmpty()) {
                    return@withContext Result.failure(Exception("Teks kosong"))
                }

                Log.d("SummaryRepo", "Calling API for summary, text length: ${extractedText.length}")

                // Panggil API Railway untuk ringkasan
                val response = RetrofitClient.api.summarizeText(
                    SummarizeRequest(extractedText)
                )

                if (!response.isSuccessful || response.body() == null) {
                    return@withContext Result.failure(Exception("Gagal memanggil API: ${response.code()}"))
                }

                val summaryResponse = response.body()!!
                val summaryText = summaryResponse.summary

                Log.d("SummaryRepo", "Summary received, length: ${summaryText.length}")

                // Simpan ringkasan ke Firestore
                val summaryModel = SummaryModel(
                    documentId = documentId,
                    fileName = document.fileName,
                    summary = summaryText,
                    summaryLength = summaryText.length,
                    originalLength = extractedText.length,
                    createdAt = Date(),
                    userId = userId
                )

                val summaryId = saveSummary(summaryModel)

                // Update dokumen dengan summaryId
                updateDocumentWithSummary(documentId, summaryId)

                return@withContext Result.success(summaryModel)

            } catch (e: Exception) {
                Log.e("SummaryRepo", "Error: ${e.message}", e)
                return@withContext Result.failure(e)
            }
        }
    }
}