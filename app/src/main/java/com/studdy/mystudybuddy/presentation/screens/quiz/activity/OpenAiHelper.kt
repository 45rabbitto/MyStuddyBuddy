package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

object OpenAiHelper {

    // Ganti dengan API Key OpenAI Anda
    private const val API_KEY = "sk-----"

    fun generateSoal(
        ringkasan: String,
        jumlahSoal: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val soalJson = callOpenAiForQuestions(ringkasan, jumlahSoal)
                onSuccess(soalJson)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun callOpenAiForQuestions(ringkasan: String, jumlahSoal: Int): String {
        val prompt = """
            Buatkan $jumlahSoal soal pilihan ganda dari materi berikut:
            
            $ringkasan
            
            Format output harus dalam JSON Array seperti contoh:
            [
                {
                    "question": "Pertanyaan 1?",
                    "options": ["Jawaban A", "Jawaban B", "Jawaban C", "Jawaban D"],
                    "correctAnswer": 0
                }
            ]
            
            Pastikan:
            1. correctAnswer adalah index 0-3 (0 untuk A, 1 untuk B, dst)
            2. Soal harus relevan dengan materi di atas
            3. Gunakan bahasa Indonesia
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "Kamu adalah asisten yang membuat soal quiz pilihan ganda.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 2000)
        }

        val url = URL("https://api.openai.com/v1/chat/completions")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $API_KEY")
        connection.doOutput = true
        connection.connectTimeout = 30000
        connection.readTimeout = 30000

        connection.outputStream.use { os: OutputStream ->
            os.write(jsonBody.toString().toByteArray())
        }

        val responseCode = connection.responseCode
        val response = if (responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.bufferedReader().readText()
        } else {
            connection.errorStream?.bufferedReader()?.readText() ?: ""
        }
        connection.disconnect()

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("API Error: $responseCode - $response")
        }

        val jsonResponse = JSONObject(response)
        val content = jsonResponse
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()

        return content
    }
}