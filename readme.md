# 📚 My Study Buddy

Aplikasi AI Study Assistant Terintegrasi untuk Membantu Pelajar Meningkatkan Efektivitas Belajar, Membiasakan Self-Learning, dan Meningkatkan daya ingat terhadap Materi.

---

## 📖 Deskripsi Singkat Proyek

My Study Buddy adalah aplikasi pembelajaran berbasis AI yang dirancang untuk membantu pelajar dalam memahami materi secara lebih efektif dan efisien. Aplikasi ini menyediakan 5 fitur dalam satu platform terpadu.

**Fitur Utama:**

1. **Ringkasan Otomatis** - Upload dokumen (PDF) dan dapatkan ringkasan materi plus poin penting secara instan menggunakan AI
2. **Kuis Interaktif** - Soal kuis dihasilkan otomatis dari materi dengan jumlah customizable oleh pengguna
3. **AI Chatbot** - Tanya jawab tentang materi dengan chatbot yang memahami konteks dokumen yang sudah diupload
4. **Monitoring Progres** - Lacak perkembangan belajar melalui grafik dan statistik performa
5. **Rekomendasi Personal** - Dapatkan saran topik belajar selanjutnya berdasarkan riwayat belajar

**Target Pengguna:** Pelajar, mahasiswa, dan siapa pun yang ingin belajar mandiri dengan bantuan AI.

**Teknologi yang Digunakan:**
- Frontend (Android): Kotlin, Jetpack Compose, XML Views
- Backend (AI): Python, FastAPI railways, Openroute API, onnx
- Database: Firebase Firestore, Firebase Realtime Database 
- Model Ringkasan: onnx- bertlite
- Deployment model ringkasan: Railway 
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)

---
## ⭐ Tautan download dan cara instalasi: 
- download apk melalui https://drive.google.com/drive/folders/1eR9D7ea_NhQ0MDDiP1DAcRHCRRkdfi17?usp=sharing
- install pada smartphone android
- berikan akses aplikasi dan pastikan aplikasi tersambung pada internet
---

## 🛠️ Petunjuk Setup Environment

### Prasyarat Sistem

Sebelum memulai, pastikan komputer Anda memenuhi persyaratan berikut:

- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17 atau lebih tinggi
- Python 3.10 atau lebih tinggi
- RAM minimal 8 GB (16 GB direkomendasikan)
- Sistem Operasi: Windows 10 / macOS 11 / Ubuntu 20.04 atau lebih baru

### Langkah 1: Clone Repository
git clone https://github.com/45rabbitto/MyStudyBuddy.git
cd MyStudyBuddy

### Langkah 2: Setup Backend Python 
- Clone repository backend
git clone https://github.com/indhana11/MyStudyBuddy-Backend.git
cd MyStudyBuddy-Backend
- Buat virtual environment python -m venv venv
- Aktifkan virtual environment
- Install dependencies
- pip install -r requirements.txt

### Langkah 3: Download Model Ringkasan 
- Buat folder models mkdir models
- Download model git clone https://github.com/indhana11/MyStudyBuddy-Backend atau https://drive.google.com/drive/folders/1sMJnm3-lbVQpM9aZjp9DsQo5D5nHD1j5?usp=sharing

### Langkah 4: OpenRouter API Key (Chatbot Gratis)
- Dapatkan API Key:
- Buka https://openrouter.ai/
- Buka https://openrouter.ai/keys
- Klik "Create Key"
- Buat file key di direktori "app/src/main/assets/chatbot_token.txt"
- Pastekan key kedalam direktori tersebut

### Langkah 5: Deploy Backend ke Railway
Buka https://railway.app/
- Klik New Project → Deploy from GitHub
- Pilih repository MyStudyBuddy-Backend
- Set Environment Variables: PORT	8000
- Klik Deploy
- Pastikan deploy berhasil 

### Langkah 6: Buka Proyek di Android Studio
- Buka Android Studio
- Pilih File → Open
- Arahkan ke folder MyStudyBuddy
- Tunggu proses Gradle Sync selesai

### Langkah 7: Konfigurasi URL Backend di Android
- pada file " network/RetrofitClient.kt" isi:
> private const val BASE_URL = "https://mystudybuddy-backend-production.up.railway.app/" 
- pada file "utils/ChatbotApiService.kt" isi:
> private val BASE_URL = "https://openrouter.ai/api/v1/chat/completions"
> private val MODEL_NAME = "openrouter/free"

### Langkah 8: Setup Firebase
- Buat project di Firebase Console
- Download google-services.json
- Letakkan di folder app/
- Aktifkan Firestore Database dan Realtime Database

### Langkah 9: Sync Gradle & Run
Menjalankan Aplikasi Android:
- Buka Android Studio
- Pilih emulator atau HP fisik
- Klik tombol Run (▶)
- Atau build APK

---

### TAUTAN MODEL:
- Model AI Ringkasan: https://github.com/indhana11/MyStudyBuddy-Backend atau https://drive.google.com/drive/folders/1sMJnm3-lbVQpM9aZjp9DsQo5D5nHD1j5?usp=sharing
- Chatbot	OpenRouter (DeepSeek/Llama/Phi)	https://openrouter.ai/models

---

## Troubleshooting
- **Backend 502 Bad Gateway**: Redeploy backend di Railway, cek log apakah model berhasil dimuat. Buka Railway Dashboard → Deployment → Redeploy.
- **Missing Token**: Pastikan file `chatbot_token.txt` ada di folder `app/src/main/assets/` dan berisi OpenRouter API Key yang valid. File ini tidak boleh kosong.
- **PDF tidak terbaca**: Pastikan file PDF berisi teks (bukan hasil scan/gambar). Anda bisa coba buka PDF di komputer dan coba copy teksnya. Jika tidak bisa di-copy, PDF tersebut hanya berisi gambar.
- **Firestore tidak terhubung**: Cek file `google-services.json` sudah benar dan diletakkan di folder `app/`. Pastikan aturan security Firestore di set ke `allow read, write: if true` untuk development.
- **Rate limit OpenRouter**: Gunakan model `openrouter/free` yang otomatis memilih model gratis terbaik. Jika masih kena limit, tunggu 30-60 detik sebelum mencoba lagi.
- **Aplikasi force close saat upload PDF**: Pastikan file PDF tidak terlalu besar (maksimal 10MB). PDFBox Android mungkin kehabisan memori jika file terlalu besar.
- **Ringkasan tidak muncul**: Cek koneksi internet. Backend Railway harus bisa diakses. Buka `https://mystudybuddy-backend-production.up.railway.app/health` di browser. Jika tidak bisa diakses, backend sedang mati.
- **Chatbot tidak merespon**: Cek file `chatbot_token.txt` berisi API Key yang valid. Buka Logcat dan filter dengan `CHATBOT_API` untuk melihat error detail.
- **Gradle sync gagal**: Pastikan koneksi internet stabil. Coba `File → Invalidate Caches → Invalidate and Restart`. Jika masih gagal, cek `build.gradle.kts` tidak ada syntax error.
- **Emulator tidak bisa akses internet**: Restart emulator. Pastikan komputer terhubung ke internet. Coba buka browser di emulator dan akses google.com.

---

## 👥 Tim Pengembang PJK-GM012

- **Project Manager** - APC466D6X0165 - Desi Triana
- **Android Developer 1** - APC466D6X0112 - Kharisma Nur Aulia
- **Android Developer 1** - APC466D6X0174 - Khariska Melly Salsabila 
- **Data Scientist** - APC349D6X0209 - Keysya Aulia
- **AI Engineer**** - APC466D6X0416 - Indhana Zulfa Mu'Azzizah

---

## 📞 Kontak & Informasi Lebih Lanjut

- **Repository Android:** https://github.com/45rabbitto/MyStudyBuddy
- **Repository Backend:** https://github.com/indhana11/MyStudyBuddy-Backend
- **Repository Model:** https://github.com/45rabbitto/ai-MyStudyBuddy atau https://drive.google.com/drive/folders/1sMJnm3-lbVQpM9aZjp9DsQo5D5nHD1j5?usp=sharing
- **OpenRouter (API Chatbot Gratis):** https://openrouter.ai/
- **Railway (Hosting Backend):** https://railway.app/
- **Firebase Console:** https://console.firebase.google.com/

