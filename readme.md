# 📚 My Study Buddy

Aplikasi AI Study Assistant Terintegrasi untuk Membantu Pelajar Meningkatkan Efektivitas Belajar, Membiasakan Self-Learning, dan Meningkatkan daya ingat terhadap Materi.

---

## 📖 Deskripsi Singkat Proyek

My Study Buddy adalah aplikasi pembelajaran berbasis AI yang dirancang untuk membantu pelajar dalam memahami materi secara lebih efektif dan efisien. Aplikasi ini menyediakan 5 fitur utama dalam satu platform terpadu.

**Fitur Utama:**

1. **Ringkasan Otomatis** - Upload dokumen (PDF/TXT/DOCX) dan dapatkan ringkasan materi plus poin penting secara instan menggunakan AI DeepSeek

2. **Kuis Interaktif** - Soal kuis dihasilkan otomatis dari materi dengan tingkat kesulitan yang menyesuaikan kemampuan pengguna

3. **AI Chatbot** - Tanya jawab tentang materi dengan chatbot berbasis DeepSeek yang memahami konteks dokumen yang sudah diupload

4. **Monitoring Progres** - Lacak perkembangan belajar melalui grafik dan statistik performa

5. **Rekomendasi Personal** - Dapatkan saran topik belajar selanjutnya berdasarkan kemampuan dan riwayat belajar

**Target Pengguna:** Pelajar, mahasiswa, dan siapa pun yang ingin belajar mandiri dengan bantuan AI.

**Teknologi yang Digunakan:**
- Frontend (Android): Kotlin, Jetpack Compose, Material 3, MVVM, Room Database, Hilt
- Backend (AI): Python, FastAPI, DeepSeek API, Transformers
- Model Ringkasan: Lokal (disimpan di folder `models/`)
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)

---

## 🛠️ Petunjuk Setup Environment

### Prasyarat Sistem

Sebelum memulai, pastikan komputer Anda memenuhi persyaratan berikut:

- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17 atau lebih tinggi
- Python 3.10 atau lebih tinggi
- RAM minimal 8 GB (16 GB direkomendasikan)
- Storage kosong minimal 4 GB (ditambah model AI ~2GB)
- Sistem Operasi: Windows 10 / macOS 11 / Ubuntu 20.04 atau lebih baru

### Langkah 1: Clone Repository
git clone https://github.com/kelompok5/MyStudyBuddy-Android.git
cd MyStudyBuddy-Android


### Langkah 2: Setup Backend Python 
# Clone repository backend
git clone https://github.com/kelompok5/MyStudyBuddy-Backend.git
cd MyStudyBuddy-Backend

# Buat virtual environment (opsional tapi direkomendasikan)
python -m venv venv

# Aktifkan virtual environment
# Untuk Windows:
venv\Scripts\activate
# Untuk Mac/Linux:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

Langkah 3: Download Model Ringkasan (Lokal)
# Buat folder models di dalam folder backend
mkdir models

# Download model dari HuggingFace (sekitar 1.5GB)
# Model yang digunakan: DeepSeek-R1-Distill-Qwen-1.5B

# Menggunakan git lfs (recommended)
git lfs install
git clone https://huggingface.co/deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B models/deepseek-summarizer

# Atau download manual dari:
# https://huggingface.co/deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B/tree/main

###Langkah 4: Setup API Key DeepSeek
#isi file env
DEEPSEEK_API_KEY=your_deepseek_api_key_here
DEEPSEEK_BASE_URL=https://api.deepseek.com/v1
MODEL_PATH=./models/deepseek-summarizer
PORT=8000

###Langkah 5: Menjalankan Backend Server
# Pastikan virtual environment aktif
# Untuk Windows:
venv\Scripts\activate
# Untuk Mac/Linux:
source venv/bin/activate

# Jalankan server FastAPI
python app.py

# Atau menggunakan uvicorn langsung:
uvicorn app:app --host 0.0.0.0 --port 8000 --reload


###Langkah 6: Buka Proyek di Android Studio

###Langkah 7: Konfigurasi URL Backend di Android


###download model yang telah disediakan, masukkan di folder python 

### sync kan gradle 
