package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.presentation.screens.upload.activity.UploadActivity

class AlurActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgLogo: ImageView
    private lateinit var tvRekomendasi: TextView
    private lateinit var btnKuis: Button
    private lateinit var tvNextStep: TextView
    private lateinit var tvTopikUtama: TextView
    private lateinit var tvSubtitleTopik: TextView
    private lateinit var tvJudulTopik: TextView

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance().reference

    // =========================
    // PESAN UNTUK KONDISI BELUM ADA DATA
    // =========================

    private val pesanBelumAdaData = """
        Kamu belum mengupload materi atau mengerjakan kuis.

        Upload materi terlebih dahulu, baca ringkasannya, lalu kerjakan kuis agar sistem dapat memberikan rekomendasi belajar yang sesuai dengan kemampuanmu.
    """.trimIndent()

    // =========================
    // VARIASI KALIMAT REKOMENDASI
    // (dipilih acak agar tidak terasa template/statis)
    // =========================

    private val rekomendasiRendah = listOf(
        """
        📚 Sepertinya kamu masih perlu memperkuat pemahaman dasar pada materi ini, jadi luangkan waktu sebentar untuk membaca ulang ringkasan materi secara perlahan agar konsep-konsep utamanya benar-benar tertanam di kepala kamu.

        Setelah itu, cobalah memahami kembali istilah-istilah penting yang sering muncul, lalu tonton beberapa video pembelajaran dasar yang membahas topik yang sama supaya penjelasannya bisa kamu lihat dari sudut pandang yang berbeda.
        """.trimIndent(),

        """
        🌱 Nilai kuis kamu menunjukkan bahwa pondasi pemahamanmu pada topik ini masih perlu dikuatkan lagi, jadi jangan terburu-buru untuk lanjut ke materi berikutnya ya.

        Coba luangkan waktu untuk membaca ulang ringkasan materi sambil mencatat poin-poin yang menurutmu masih membingungkan, kemudian cari penjelasan tambahan dari video pembelajaran dasar agar pemahamanmu makin matang sebelum mencoba kuis lagi.
        """.trimIndent(),

        """
        💡 Sepertinya beberapa konsep dasar pada materi ini belum sepenuhnya kamu pahami, dan itu wajar banget kok, jadi jangan khawatir.

        Sebagai langkah awal, pelajari kembali ringkasan materinya pelan-pelan, beri perhatian khusus pada istilah-istilah kunci, lalu lengkapi pemahamanmu dengan menonton video pembelajaran dasar sebelum kembali mengerjakan kuis.
        """.trimIndent()
    )

    private val rekomendasiSedang = listOf(
        """
        ✏️ Kemampuan kamu pada materi ini sudah cukup baik, namun masih ada ruang untuk berkembang lebih jauh lagi, jadi sayang banget kalau berhenti di sini.

        Coba diskusikan beberapa bagian yang masih terasa kurang yakin dengan AI Chatbot, lalu lanjutkan dengan latihan soal tingkat menengah dan buat rangkuman versi kamu sendiri agar pemahamannya makin melekat.
        """.trimIndent(),

        """
        🙂 Hasil kuis kamu menunjukkan pemahaman yang lumayan solid pada materi ini, tapi sepertinya masih ada beberapa bagian yang perlu dipertajam lagi sedikit.

        Manfaatkan AI Chatbot untuk bertanya tentang poin-poin yang masih membingungkan, kemudian coba kerjakan latihan soal tingkat menengah dan pelajari studi kasus sederhana untuk melatih penerapan konsepnya.
        """.trimIndent(),

        """
        📈 Kamu sudah berada di jalur yang baik untuk materi ini, hanya saja masih ada beberapa detail yang sebaiknya kamu pahami lebih dalam lagi.

        Cobalah berdiskusi dengan AI Chatbot mengenai bagian yang masih terasa abu-abu, lalu lanjutkan dengan latihan soal tingkat menengah serta mulai membuat rangkuman sendiri agar materinya makin nempel di kepala.
        """.trimIndent()
    )

    private val rekomendasiTinggi = listOf(
        """
        🚀 Kemampuan kamu pada materi ini sudah sangat baik, jadi ini saat yang tepat untuk melangkah ke level yang lebih menantang lagi.

        Coba kerjakan kuis berikutnya untuk menguji pemahamanmu pada topik lanjutan, lalu lengkapi dengan studi kasus lanjutan, project mini, serta latihan soal HOTS agar kemampuan analisismu makin terlatih.
        """.trimIndent(),

        """
        🌟 Hasil kuis kamu menunjukkan penguasaan materi yang sangat solid, jadi jangan berhenti di sini ya, lanjutkan momentum belajarmu.

        Kerjakan kuis kembali untuk masuk ke topik selanjutnya, dan sambil itu cobalah studi kasus lanjutan, project mini sederhana, serta latihan soal HOTS untuk mengasah pemahamanmu lebih jauh lagi.
        """.trimIndent(),

        """
        🔥 Selamat, performa kamu pada materi ini sangat memuaskan, dan ini menjadi sinyal kalau kamu siap untuk tantangan yang lebih tinggi.

        Lanjutkan dengan mengerjakan kuis kembali untuk topik berikutnya, sambil mengeksplorasi studi kasus lanjutan, project mini, dan latihan soal HOTS yang akan membantu mempertajam kemampuan berpikirmu.
        """.trimIndent()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alur)

        initViews()
        setupListeners()
        loadRekomendasi()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)

        imgLogo = findViewById(R.id.imgLogo)

        tvRekomendasi =
            findViewById(R.id.tvRekomendasi)

        btnKuis =
            findViewById(R.id.btnBukaQuiz)

        tvNextStep =
            findViewById(R.id.tvMulaiLatihan)

        tvTopikUtama =
            findViewById(R.id.tvTopikUtama)

        tvSubtitleTopik =
            findViewById(R.id.tvSubtitleTopik)

        tvJudulTopik =
            findViewById(R.id.tvFile)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        imgLogo.setOnClickListener {

            Toast.makeText(
                this,
                "Rekomendasi aktif",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnKuis.setOnClickListener {

            val fileName =
                intent.getStringExtra("FILE_NAME")

            startActivity(
                Intent(
                    this,
                    QuizActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )
                }
            )
        }

        tvNextStep.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UploadActivity::class.java
                )
            )
        }
    }

    private fun loadRekomendasi() {

        val uid =
            auth.currentUser?.uid

        if (uid == null) {
            tvJudulTopik.text =
                "Belum Ada Materi"

            tvTopikUtama.text =
                "• Belum ada file yang diupload"

            tvSubtitleTopik.text =
                "Upload materi untuk mulai belajar"

            tvRekomendasi.text =
                pesanBelumAdaData

            return
        }

        database.child("QuizHistory")
            .child(uid)
            .limitToLast(1)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            // Belum pernah mengerjakan kuis sama sekali
                            tvJudulTopik.text =
                                "Belum Ada Materi"

                            tvTopikUtama.text =
                                "• Belum ada file yang diupload"

                            tvSubtitleTopik.text =
                                "Upload materi untuk mulai belajar"

                            tvRekomendasi.text =
                                pesanBelumAdaData

                            return
                        }

                        var score = 0
                        var materi = "Materi"

                        for (data in snapshot.children) {

                            score =
                                data.child("score")
                                    .getValue(Int::class.java)
                                    ?: 0

                            materi =
                                data.child("fileName")
                                    .getValue(String::class.java)
                                    ?: "Materi"
                        }

                        val rekomendasi =
                            when {
                                score < 50 -> rekomendasiRendah.random()
                                score <= 75 -> rekomendasiSedang.random()
                                else -> rekomendasiTinggi.random()
                            }

                        val statusTopik =
                            when {
                                score < 50 -> "Perlu latihan"
                                score <= 75 -> "Cukup baik"
                                else -> "Sudah dikuasai"
                            }

                        val judulTopik =
                            when {
                                score < 50 -> "Topik yang Perlu Ditingkatkan"
                                score <= 75 -> "Topik yang Sedang Dipelajari"
                                else -> "Topik yang Sudah Dikuasai"
                            }

                        tvRekomendasi.text =
                            rekomendasi

                        tvJudulTopik.text =
                            judulTopik

                        tvTopikUtama.text =
                            "• $materi ($statusTopik)"

                        tvSubtitleTopik.text =
                            "Berdasarkan hasil quiz terakhir (skor: $score)"
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        tvRekomendasi.text =
                            "Gagal memuat rekomendasi"
                    }
                }
            )
    }
}