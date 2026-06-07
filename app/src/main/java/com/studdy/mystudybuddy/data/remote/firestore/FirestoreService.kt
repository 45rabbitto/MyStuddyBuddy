package com.studdy.mystudybuddy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.data.models.DocumentModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val COLLECTION_DOCUMENTS = "documents"

    /**
     * Simpan dokumen ke Firestore
     */
    suspend fun saveDocument(document: DocumentModel): String {
        val docId = firestore.collection(COLLECTION_DOCUMENTS).document().id
        val docWithId = document.copy(id = docId)
        firestore.collection(COLLECTION_DOCUMENTS).document(docId).set(docWithId).await()
        return docId
    }

    /**
     * Ambil semua dokumen user
     */
    suspend fun getAllDocuments(userId: String): List<DocumentModel> {
        val snapshot = firestore.collection(COLLECTION_DOCUMENTS)
            .whereEqualTo("userId", userId)
            .orderBy("uploadedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(DocumentModel::class.java) }
    }

    /**
     * Ambil satu dokumen by ID
     */
    suspend fun getDocumentById(documentId: String): DocumentModel? {
        val doc = firestore.collection(COLLECTION_DOCUMENTS).document(documentId).get().await()
        return doc.toObject(DocumentModel::class.java)
    }
}