package com.studdy.mystudybuddy.data.repository

import android.util.Log
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

    private val COLLECTION_PDF_CONTENTS = "PdfContents"
    private val COLLECTION_DOCUMENTS = "documents"
    private val COLLECTION_SUMMARIES = "summaries"

    // 🔥 Simpan dokumen ke PdfContents/documents
    suspend fun saveDocument(
        fileName: String,
        content: String,
        userId: String
    ): String {
        return withContext(Dispatchers.IO) {
            val document = DocumentModel(
                fileName = fileName,
                content = content,
                userId = userId,
                uploadedAt = Date(),
                isProcessed = false
            )
            // Simpan ke PdfContents/{userId}/documents/{autoId}
            val docRef = firestore.collection(COLLECTION_PDF_CONTENTS)
                .document(userId)
                .collection(COLLECTION_DOCUMENTS)
                .document()
            docRef.set(document).await()
            Log.d("SummaryRepo", "Document saved: ${docRef.id}")
            return@withContext docRef.id
        }
    }

    // 🔥 Ambil dokumen dari PdfContents/{userId}/documents/{documentId}
    suspend fun getDocumentById(userId: String, documentId: String): DocumentModel? {
        return withContext(Dispatchers.IO) {
            val doc = firestore.collection(COLLECTION_PDF_CONTENTS)
                .document(userId)
                .collection(COLLECTION_DOCUMENTS)
                .document(documentId)
                .get()
                .await()
            doc.toObject(DocumentModel::class.java)?.copy(id = doc.id)
        }
    }

    // 🔥 Simpan ringkasan ke collection "summaries" (terpisah)
    suspend fun saveSummary(summaryModel: SummaryModel): String {
        return withContext(Dispatchers.IO) {
            val docRef = firestore.collection(COLLECTION_SUMMARIES).document()
            docRef.set(summaryModel).await()
            Log.d("SummaryRepo", "Summary saved: ${docRef.id}")
            return@withContext docRef.id
        }
    }

    // 🔥 Update dokumen dengan summaryId
    suspend fun updateDocumentWithSummary(userId: String, documentId: String, summaryId: String) {
        withContext(Dispatchers.IO) {
            firestore.collection(COLLECTION_PDF_CONTENTS)
                .document(userId)
                .collection(COLLECTION_DOCUMENTS)
                .document(documentId)
                .update("isProcessed", true, "summaryId", summaryId)
                .await()
            Log.d("SummaryRepo", "Document updated with summaryId: $summaryId")
        }
    }
}