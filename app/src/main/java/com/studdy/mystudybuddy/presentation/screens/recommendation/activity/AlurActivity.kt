package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.history.activity.FileHistoryActivity
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity
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

    private val firestore =
        FirebaseFirestore.getInstance()

    private var fileNameTerakhir: String = ""

    private val rekomendasiRendah = listOf(
        """
        📚 Sepertinya kamu masih perlu memperkuat pemahaman dasar pada materi ini, jadi luangkan waktu sebentar untuk membaca ulang ringkasan materi secara perlahan agar konsep-konsep utamanya benar-benar tertanam di kepala kamu.

        Setelah itu, cobalah memahami kembali istilah-istilah penting yang sering muncul, lalu lengkapi pemahamanmu dengan mencari penjelasan tambahan dari sumber lain sebelum kembali mengerjakan kuis.
        """.trimIndent(),

        """
        🌱 Nilai kuis kamu menunjukkan bahwa pondasi pemahamanmu pada topik ini masih perlu dikuatkan lagi, jadi jangan terburu-buru untuk lanjut ke materi berikutnya ya.

        Coba luangkan waktu untuk membaca ulang ringkasan materi sambil mencatat poin-poin yang menurutmu masih membingungkan, kemudian pelajari kembali bagian tersebut sebelum mencoba kuis lagi.
        """.trimIndent(),

        """
        💡 Sepertinya beberapa konsep dasar pada materi ini belum sepenuhnya kamu pahami, dan itu wajar banget kok, jadi jangan khawatir.

        Sebagai langkah awal, pelajari kembali ringkasan materinya pelan-pelan, beri perhatian khusus pada istilah-istilah kunci, lalu coba kerjakan kuis ini lagi setelah lebih siap.
        """.trimIndent(),

        """
        🧩 Hasil kuis kali ini menunjukkan masih ada beberapa bagian penting dari materi yang belum tersampaikan dengan baik ke pemahamanmu.

        Yuk baca ulang ringkasannya sekali lagi dengan lebih santai, beri tanda pada bagian yang masih membingungkan, lalu coba kerjakan kuisnya kembali setelah itu.
        """.trimIndent()
    )

    private val rekomendasiSedang = listOf(
        """
        ✏️ Kemampuan kamu pada materi ini sudah cukup baik, namun masih ada ruang untuk berkembang lebih jauh lagi, jadi sayang banget kalau berhenti di sini.

        Coba diskusikan beberapa bagian yang masih terasa kurang yakin dengan AI Chatbot, lalu setelah itu kamu bisa mencoba mengulang kuis ini untuk memantapkan pemahamanmu.
        """.trimIndent(),

        """
        🙂 Hasil kuis kamu menunjukkan pemahaman yang lumayan solid pada materi ini, tapi sepertinya masih ada beberapa bagian yang perlu dipertajam lagi sedikit.

        Manfaatkan AI Chatbot untuk bertanya tentang poin-poin yang masih membingungkan, kemudian coba kerjakan kuis ini sekali lagi untuk melihat seberapa jauh pemahamanmu meningkat.
        """.trimIndent(),

        """
        📈 Kamu sudah berada di jalur yang baik untuk materi ini, hanya saja masih ada beberapa detail yang sebaiknya kamu pahami lebih dalam lagi.

        Cobalah berdiskusi dengan AI Chatbot mengenai bagian yang masih terasa abu-abu, lalu ulangi kuis ini agar nilainya bisa lebih maksimal lagi.
        """.trimIndent(),

        """
        🔍 Performamu pada materi ini sudah lumayan, tapi sepertinya ada satu dua hal yang masih perlu sedikit kamu pertajam.

        Coba tanyakan ke AI Chatbot tentang bagian yang menurutmu masih kurang yakin, lalu kerjakan kuisnya lagi supaya hasilnya makin maksimal.
        """.trimIndent()
    )

    private val rekomendasiTinggi = listOf(
        """
        🚀 Kemampuan kamu pada materi ini sudah sangat baik, jadi ini saat yang tepat untuk melangkah ke materi berikutnya.

        Lanjutkan dengan mengupload materi baru, lalu pelajari ringkasannya dan kerjakan kuisnya untuk terus mengasah kemampuanmu.
        """.trimIndent(),

        """
        🌟 Hasil kuis kamu menunjukkan penguasaan materi yang sangat solid, jadi jangan berhenti di sini ya, lanjutkan momentum belajarmu.

        Kamu bisa melanjutkan ke materi selanjutnya, upload materi baru, dan kerjakan kuisnya untuk terus mengembangkan pemahamanmu.
        """.trimIndent(),

        """
        🔥 Selamat, performa kamu pada materi ini sangat memuaskan, dan ini menjadi sinyal kalau kamu siap untuk tantangan yang lebih tinggi.

        Lanjutkan dengan mengupload dan mempelajari materi baru agar kemampuanmu semakin terlatih dan berkembang.
        """.trimIndent(),

        """
        🎯 Mantap, kamu sudah menguasai materi ini dengan sangat baik!

        Saatnya melangkah ke topik selanjutnya — upload materi baru, baca ringkasannya, dan uji pemahamanmu lewat kuis untuk terus naik level.
        """.trimIndent()
    )

    private val pesanBelumAdaData = """
        Kamu belum mengupload materi atau mengerjakan kuis.

        Upload materi terlebih dahulu, baca ringkasannya, lalu kerjakan kuis agar sistem dapat memberikan rekomendasi belajar yang sesuai dengan kemampuanmu.
    """.trimIndent()

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

        val uid = auth.currentUser?.uid

        if (uid == null) {
            tampilkanBelumAdaData()
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
                            tampilkanBelumAdaData()
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

                        fileNameTerakhir = materi

                        tampilkanRekomendasi(score, materi, uid)
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

    private fun tampilkanBelumAdaData() {

        tvJudulTopik.text =
            "Belum Ada Materi"

        tvTopikUtama.text =
            "• Belum ada file yang diupload"

        tvSubtitleTopik.text =
            "Upload materi untuk mulai belajar"

        tvRekomendasi.text =
            pesanBelumAdaData

        btnKuis.visibility =
            View.GONE
    }

    private fun tampilkanRekomendasi(
        score: Int,
        materi: String,
        uid: String
    ) {

        val rekomendasi =
            when {
                score < 50 -> rekomendasiRendah.random()
                score < 80 -> rekomendasiSedang.random()
                else -> rekomendasiTinggi.random()
            }

        val statusTopik =
            when {
                score < 50 -> "Perlu latihan"
                score < 80 -> "Cukup baik"
                else -> "Sudah dikuasai"
            }

        val judulTopik =
            when {
                score < 50 -> "Topik yang Perlu Ditingkatkan"
                score < 80 -> "Topik yang Sedang Dipelajari"
                else -> "Topik yang Sudah Dikuasai"
            }

        tvRekomendasi.text = rekomendasi
        tvJudulTopik.text = judulTopik
        tvTopikUtama.text = "• $materi ($statusTopik)"
        tvSubtitleTopik.text = "Berdasarkan hasil quiz terakhir (skor: $score)"

        when {

            // Skor rendah -> buka halaman History File
            score < 50 -> {

                btnKuis.visibility = View.VISIBLE
                btnKuis.text = "Pelajari Materi Kembali"

                btnKuis.setOnClickListener {
                    startActivity(
                        Intent(
                            this,
                            FileHistoryActivity::class.java
                        )
                    )
                }
            }

            // Skor sedang -> ulangi kuis dengan materi yang sama
            score < 80 -> {

                btnKuis.visibility = View.VISIBLE
                btnKuis.text = "Ulangi Kuis"

                btnKuis.setOnClickListener {
                    ulangiKuis(uid, materi)
                }
            }

            // Skor tinggi -> sembunyikan tombol
            else -> {
                btnKuis.visibility = View.GONE
            }
        }
    }

    private fun ambilDataMateri(
        uid: String,
        fileName: String,
        onResult: (
            documentId: String,
            ringkasan: String,
            materiAsli: String
        ) -> Unit,
        onError: () -> Unit
    ) {

        firestore.collection("summaries")
            .whereEqualTo("userId", uid)
            .whereEqualTo("fileName", fileName)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->

                val doc = result.documents.firstOrNull()

                if (doc == null) {
                    onError()
                    return@addOnSuccessListener
                }

                val ringkasan = doc.getString("summary") ?: ""
                val documentId = doc.getString("documentId") ?: ""

                if (documentId.isEmpty()) {
                    onResult(documentId, ringkasan, "")
                    return@addOnSuccessListener
                }

                firestore.collection("documents")
                    .document(documentId)
                    .get()
                    .addOnSuccessListener { docSnapshot ->

                        val materiAsli =
                            docSnapshot.getString("content") ?: ""

                        onResult(documentId, ringkasan, materiAsli)
                    }
                    .addOnFailureListener {
                        onResult(documentId, ringkasan, "")
                    }
            }
            .addOnFailureListener {
                onError()
            }
    }

    private fun ulangiKuis(uid: String, fileName: String) {

        Toast.makeText(
            this,
            "Menyiapkan kuis...",
            Toast.LENGTH_SHORT
        ).show()

        ambilDataMateri(
            uid,
            fileName,
            onResult = { _, ringkasan, materiAsli ->

                startActivity(
                    Intent(
                        this,
                        QuizActivity::class.java
                    ).apply {
                        putExtra("FILE_NAME", fileName)
                        putExtra("RINGKASAN", ringkasan)
                        putExtra("MATERI_ASLI", materiAsli)
                    }
                )
            },
            onError = {
                Toast.makeText(
                    this,
                    "Gagal memuat materi untuk kuis",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}