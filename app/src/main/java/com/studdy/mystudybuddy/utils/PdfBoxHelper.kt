package com.studdy.mystudybuddy.utils

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

object PdfBoxHelper {

    fun extractTextFromPdf(
        context: Context,
        uri: Uri
    ): String {

        PDFBoxResourceLoader.init(context)

        return try {

            context.contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->

                    val document =
                        PDDocument.load(inputStream)

                    val text =
                        PDFTextStripper()
                            .getText(document)

                    document.close()

                    text
                } ?: ""

        } catch (e: Exception) {

            e.printStackTrace()
            ""
        }
    }
}