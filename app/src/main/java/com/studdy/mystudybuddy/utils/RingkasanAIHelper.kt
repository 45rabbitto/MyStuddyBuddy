package com.studdy.mystudybuddy.utils

import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RingkasanAIHelper(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // Untuk emulator Android (10.0.2.2 = localhost komputer)
    private val BASE_URL = "http://10.0.2.2:8000"

    // Untuk HP fisik, ganti dengan IP komputer (contoh: http://192.168.1.100:8000)
    // private val BASE_URL = "http://192.168.1.100:8000"

    /**
     * Kirim teks ke backend Python (MobileBERT ONNX)
     * dan dapatkan ringkasan
     */
    suspend fun prosesRingkasan(teks: String): String {
        return try {
            // Validasi teks
            if (teks.length < 50) {
                return "Teks terlalu pendek (minimal 50 karakter). Panjang teks: ${teks.length} karakter"
            }

            // Buat request body
            val jsonBody = JSONObject().apply {
                put("text", teks)
            }.toString()

            // Buat request
            val request = Request.Builder()
                .url("$BASE_URL/summarize")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            // Eksekusi request
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonResponse = JSONObject(responseBody)
                val summary = jsonResponse.getString("summary")

                return if (summary.isNotBlank()) {
                    summary
                } else {
                    "Ringkasan berhasil dibuat tetapi kosong. Coba upload teks yang lebih panjang."
                }
            } else {
                return "Error dari server: ${response.code}\nDetail: $responseBody"
            }

        } catch (e: java.net.ConnectException) {
            return "Tidak dapat terhubung ke server AI. Pastikan backend Python berjalan di http://10.0.2.2:8000\n\nError: ${e.message}"
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error saat memproses ringkasan: ${e.message}"
        }
    }

    /**
     * Cek kesehatan backend
     */
    suspend fun cekKesehatanBackend(): Boolean {
        return try {
            val request = Request.Builder()
                .url("$BASE_URL/health")
                .get()
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}