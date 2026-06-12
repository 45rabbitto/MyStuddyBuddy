package com.studdy.mystudybuddy.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

object LoggingHelper {

    private val firestore = FirebaseFirestore.getInstance()

    fun logTextLength(
        documentId: String,
        type: String,
        text: String,
        originalLength: Int,
        userId: String = "anonymous",
        fileName: String = ""
    ) {

        Log.d("TEST_LOGGING", "logTextLength dipanggil")

        val summaryLength = text.length

        val docRef = firestore.collection("summaries").document()

        val summaryData = hashMapOf<String, Any>(
            "id" to docRef.id,
            "documentId" to documentId,
            "fileName" to fileName,
            "type" to type,
            "summary" to text,
            "summaryLength" to summaryLength,
            "originalLength" to originalLength,
            "userId" to userId,
            "createdAt" to Date()
        )

        docRef.set(summaryData)
            .addOnSuccessListener {

                Log.d(
                    "LOGGING",
                    "Summary berhasil disimpan"
                )

                val compressionRatio =
                    if (originalLength > 0)
                        summaryLength.toDouble() / originalLength.toDouble()
                    else
                        0.0

                val logData = hashMapOf<String, Any>(
                    "event" to "summary_created",
                    "summaryDocId" to docRef.id,
                    "documentId" to documentId,
                    "fileName" to fileName,
                    "originalLength" to originalLength,
                    "summaryLength" to summaryLength,

                    // perbandingan ringkasan terhadap teks asli
                    "compressionRatio" to compressionRatio,

                    // dalam persen
                    "compressionPercentage" to compressionRatio * 100,

                    // format string misalnya "2500/10000"
                    "lengthComparison" to "$summaryLength/$originalLength",

                    "userId" to userId,
                    "timestamp" to Date()
                )

                Log.d(
                    "TEST_LOGGING",
                    "Akan menyimpan ke collection logs"
                )

                firestore.collection("logs")
                    .add(logData)
                    .addOnSuccessListener { documentReference ->

                        Log.d(
                            "LOGGING",
                            "Root logs berhasil disimpan : ${documentReference.id}"
                        )
                    }
                    .addOnFailureListener { e ->

                        Log.e(
                            "LOGGING",
                            "Gagal menyimpan root logs : ${e.message}"
                        )
                    }
            }
            .addOnFailureListener { e ->

                Log.e(
                    "LOGGING",
                    "Gagal menyimpan summary : ${e.message}"
                )
            }
    }
}