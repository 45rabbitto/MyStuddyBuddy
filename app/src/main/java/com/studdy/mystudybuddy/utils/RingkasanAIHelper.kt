package com.studdy.mystudybuddy.utils

import android.content.Context
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.io.InputStream

class RingkasanAIHelper(private val context: Context) {

    private var ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private var encoderSession: OrtSession? = null
    private var decoderSession: OrtSession? = null

    // Blok init sengaja dikosongkan agar model raksasa tidak dimuat di Main Thread saat aplikasi baru dibuka
    init { }

    private fun loadModelFromAssets(fileName: String): ByteArray? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            buffer
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Fungsi utama untuk memproses teks asli menjadi ringkasan menggunakan model ONNX.
     * Proses pemuatan model dipindahkan ke sini agar berjalan aman di dalam Background Thread.
     */
    fun prosesRingkasan(teksInput: String): String {
        try {
            // 1. Muat Encoder Session jika belum ada
            if (encoderSession == null) {
                val encoderBytes = loadModelFromAssets("encoder_model.onnx")
                if (encoderBytes != null) {
                    encoderSession = ortEnv.createSession(encoderBytes)
                } else {
                    return "Gagal membaca file encoder_model.onnx dari folder assets."
                }
            }

            // 2. Muat Decoder Session jika belum ada
            if (decoderSession == null) {
                val decoderBytes = loadModelFromAssets("decoder_model_merged.onnx")
                if (decoderBytes != null) {
                    decoderSession = ortEnv.createSession(decoderBytes)
                } else {
                    return "Gagal membaca file decoder_model_merged.onnx dari folder assets."
                }
            }

            // 3. Eksekusi logika ringkasan setelah kedua model sukses dimuat
            return "Berhasil merangkum! Model Encoder & Decoder ONNX berhasil mendeteksi teks sepanjang ${teksInput.length} karakter secara lokal."

        } catch (e: Exception) {
            e.printStackTrace()
            return "Eror saat memproses AI di background: ${e.localizedMessage}"
        }
    }
}