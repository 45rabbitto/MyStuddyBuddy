package com.yourapp.data.repository

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class SummaryRepository(
    private val context: Context,
    private val storage: FirebaseStorage
) {

    /**
     * Download file PDF dari Firebase Storage
     * @param path Path file di Firebase Storage (contoh: "documents/teks_contoh.pdf")
     * @return File lokal hasil download
     */
    suspend fun downloadPdfFromFirebase(path: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val localFile = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")

            val storageRef = storage.reference.child(path)
            storageRef.getFile(localFile).await()

            Result.success(localFile)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    suspend fun getTextFromFirebase(path: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val storageRef = storage.reference.child(path)
            val bytes = storageRef.getBytes(1024 * 1024).await() // Max 1MB
            val text = String(bytes, Charsets.UTF_8)
            Result.success(text)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    suspend fun extractTextFromPdf(pdfFile: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Opsi 1: Pake Tabula (untuk PDF sederhana)
            // Opsi 2: Pake PDFBox Android (lebih berat)
            // Opsi 3: Pake Apache PDFBox (rekomendasi)

            // Untuk sementara, kita asumsikan ada library PDF parser
            // Atau bisa juga baca PDF di Python (tapi pake Chaquopy)
            val text = extractPdfWithPython(pdfFile)

            Result.success(text)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Ekstrak PDF menggunakan Python (via Chaquopy)
     */
    private suspend fun extractPdfWithPython(pdfFile: File): String = withContext(Dispatchers.IO) {
        try {
            val py = Python.getInstance().getModule("preprocess")
            val text = py.callAttr("extract_pdf_direct", pdfFile.absolutePath).toString()
            text
        } catch (e: Exception) {
            // Fallback: baca langsung sebagai teks jika file bukan PDF
            pdfFile.readText()
        }
    }

    /**
     * Hapus file temporer
     */
    fun cleanupTempFile(file: File) {
        if (file.exists()) {
            file.delete()
        }
    }
}