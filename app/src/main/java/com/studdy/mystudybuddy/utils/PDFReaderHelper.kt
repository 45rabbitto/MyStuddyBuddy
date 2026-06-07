package com.studdy.mystudybuddy.utils

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PDFReaderHelper(private val context: Context) {

    suspend fun ekstrakTeksDariPDF(uri: Uri): String = withContext(Dispatchers.IO) {
        var document: PDDocument? = null
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext "Gagal membuka file: InputStream null"

            document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)

            document.close()
            inputStream.close()

            if (text.isBlank()) {
                return@withContext "PDF berhasil dibaca, tetapi tidak ditemukan teks. File mungkin hanya berisi gambar."
            }

            // Bersihkan teks: hilangkan multiple spaces, newlines berlebih
            val cleanedText = text
                .replace(Regex("\\s+"), " ")
                .trim()

            return@withContext cleanedText

        } catch (e: OutOfMemoryError) {
            return@withContext "Error: File PDF terlalu besar untuk diproses. Coba file dengan ukuran lebih kecil."
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Gagal mengekstrak teks dari PDF: ${e.message}"
        } finally {
            document?.close()
        }
    }
}