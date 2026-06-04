package com.studdy.mystudybuddy.utils

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream

class PDFReaderHelper(private val context: Context) {

    init {
        PDFBoxResourceLoader.init(context)
    }

    /**
     * Fungsi untuk membaca seluruh teks di dalam file PDF berdasarkan URI file
     */
    fun ekstrakTeksDariPDF(fileUri: Uri): String {
        var inputStream: InputStream? = null
        var document: PDDocument? = null
        return try {
            // Membuka stream dari URI file yang dipilih user
            inputStream = context.contentResolver.openInputStream(fileUri)

            // Memuat dokumen PDF
            document = PDDocument.load(inputStream)

            // Mengambil seluruh teks menggunakan PDFTextStripper
            val stripper = PDFTextStripper()
            val teksHasil = stripper.getText(document)

            // Jika teks kosong, beri tahu user
            if (teksHasil.trim().isEmpty()) {
                "PDF berhasil dibaca, tetapi tidak ditemukan teks di dalamnya (kemungkinan berupa gambar/scan)."
            } else {
                teksHasil
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Gagal membaca file PDF: ${e.localizedMessage}"
        } finally {
            // Memastikan stream dan dokumen ditutup setelah selesai digunakan agar tidak bocor
            inputStream?.close()
            document?.close()
        }
    }
}