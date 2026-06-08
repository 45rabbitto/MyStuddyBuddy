package com.studdy.mystudybuddy.utils

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PDFUtils {

    suspend fun extractTextFromPdf(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        var document: PDDocument? = null
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext ""
            document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            inputStream.close()

            // Bersihkan teks (hilangkan multiple spaces, newlines berlebih)
            val cleanedText = text
                .replace(Regex("\\s+"), " ")
                .trim()

            return@withContext cleanedText
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ""
        } finally {
            document?.close()
        }
    }
}