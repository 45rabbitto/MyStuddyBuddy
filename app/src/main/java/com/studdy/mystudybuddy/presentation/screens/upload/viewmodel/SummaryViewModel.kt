package com.studdy.mystudybuddy.presentation.screens.upload.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.domain.usecase.GenerateSummaryUseCase
import com.yourapp.data.repository.SummaryRepository
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    private val repository: SummaryRepository
) : ViewModel() {

    private val _summaryState = MutableLiveData<SummaryState>()
    val summaryState: LiveData<SummaryState> = _summaryState

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    /**
     * Generate ringkasan dari PDF di Firebase Storage
     * @param firebasePath Path file di Firebase Storage
     */
    fun generateSummaryFromFirebasePdf(firebasePath: String) {
        viewModelScope.launch {
            _loadingState.value = true
            _summaryState.value = SummaryState.Loading

            try {
                // 1. Download PDF dari Firebase
                val downloadResult = repository.downloadPdfFromFirebase(firebasePath)

                if (downloadResult.isSuccess) {
                    val pdfFile = downloadResult.getOrNull()!!

                    // 2. Ekstrak teks dari PDF
                    val extractResult = repository.extractTextFromPdf(pdfFile)

                    if (extractResult.isSuccess) {
                        val text = extractResult.getOrNull()!!

                        // 3. Generate ringkasan
                        val summaryResult = generateSummaryUseCase(
                            context = getApplicationContext(),
                            text = text
                        )

                        if (summaryResult.isSuccess) {
                            _summaryState.value = SummaryState.Success(
                                summary = summaryResult.getOrNull()!!,
                                originalText = text
                            )
                        } else {
                            _summaryState.value = SummaryState.Error(
                                message = "Gagal generate ringkasan: ${summaryResult.exceptionOrNull()?.message}"
                            )
                        }
                    } else {
                        _summaryState.value = SummaryState.Error(
                            message = "Gagal ekstrak teks dari PDF"
                        )
                    }

                    // 4. Bersihkan file temporer
                    repository.cleanupTempFile(pdfFile)

                } else {
                    _summaryState.value = SummaryState.Error(
                        message = "Gagal download PDF dari Firebase"
                    )
                }

            } catch (e: Exception) {
                _summaryState.value = SummaryState.Error(
                    message = "Error: ${e.message}"
                )
            } finally {
                _loadingState.value = false
            }
        }
    }

    /**
     * Generate ringkasan dari teks langsung (tanpa PDF)
     */
    fun generateSummaryFromText(text: String) {
        viewModelScope.launch {
            _loadingState.value = true
            _summaryState.value = SummaryState.Loading

            val result = generateSummaryUseCase(
                context = getApplicationContext(),
                text = text
            )

            if (result.isSuccess) {
                _summaryState.value = SummaryState.Success(
                    summary = result.getOrNull()!!,
                    originalText = text
                )
            } else {
                _summaryState.value = SummaryState.Error(
                    message = "Gagal generate ringkasan: ${result.exceptionOrNull()?.message}"
                )
            }

            _loadingState.value = false
        }
    }

    // Helper untuk dapat context (perlu diimplementasikan sesuai arsitektur)
    private fun getApplicationContext(): android.content.Context {
        // Ini harus disesuaikan dengan cara kamu mengakses context
        // Bisa melalui parameter atau dependency injection
        return android.app.Application().applicationContext
    }
}

// State sealed class
sealed class SummaryState {
    object Loading : SummaryState()
    data class Success(val summary: String, val originalText: String) : SummaryState()
    data class Error(val message: String) : SummaryState()
}