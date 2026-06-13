package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log

object OpenAiHelper {

    private const val API_KEY = "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"

    fun generateSoal(
        materi: String,
        jumlahSoal: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val soalJson =
                    callOpenAiForQuestions(
                        materi,
                        jumlahSoal
                    )
                onSuccess(soalJson)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun callOpenAiForQuestions(materi: String, jumlahSoal: Int): String {
        val materiPendek =
            if (materi.length > 12000)
                materi.take(12000)
            else
                materi
        val prompt = """
            Anda adalah dosen pembuat soal.

            Berdasarkan materi berikut:
            
            $materiPendek
            
            Buatkan $jumlahSoal soal pilihan ganda.
            
            ATURAN:
            
            1. Soal HARUS berasal dari isi materi.
            2. Jangan membuat soal di luar materi.
            3. Setiap soal memiliki 4 opsi.
            4. Hanya ada 1 jawaban benar.
            5. Tingkat kesulitan menengah.
            6. Gunakan bahasa Indonesia.
            7. Jangan mengulang soal yang sama.
            
            Format JSON:
            
            [
             {
               "question":"...",
               "options":[
                  "...",
                  "...",
                  "...",
                  "..."
               ],
               "correctAnswer":0
             }
            ]
            
            Keluarkan JSON saja tanpa penjelasan tambahan.
            """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("model", "gpt-4o")
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

        val responseText =
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().readText()
            } else {
                connection.errorStream?.bufferedReader()?.readText() ?: ""
            }

        Log.d("OPENAI_CODE", responseCode.toString())
        Log.d("OPENAI_RESPONSE", responseText)

        connection.disconnect()

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("API Error: $responseCode - $responseText")
        }

        val jsonResponse = JSONObject(responseText)

        val content =
            jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()

        Log.d("QUIZ_JSON", content)

        return content
    }
}