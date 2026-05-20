package com.studdy.mystudybuddy.presentation.screens.ringkasan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RingkasanUiState(

    val isLoading: Boolean = false,

    val judul: String = "",

    val ringkasanList: List<String> = emptyList(),

    val errorMessage: String? = null
)

class RingkasanViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            RingkasanUiState()
        )

    val uiState: StateFlow<RingkasanUiState> =
        _uiState.asStateFlow()

    fun loadRingkasan() {

        // sementara data dummy
        val dataRingkasan = listOf(

            "Fotosintesis adalah proses tumbuhan menghasilkan makanan.",

            "Fotosintesis memerlukan cahaya matahari.",

            "Proses berlangsung di kloroplas.",

            "Karbon dioksida dan air diperlukan.",

            "Hasil fotosintesis menghasilkan oksigen."
        )

        _uiState.value =
            _uiState.value.copy(

                judul = "Materi Biologi",

                ringkasanList = dataRingkasan,

                isLoading = false
            )
    }

    fun setLoading(
        loading: Boolean
    ) {

        _uiState.value =
            _uiState.value.copy(
                isLoading = loading
            )
    }

    fun setError(
        message: String?
    ) {

        _uiState.value =
            _uiState.value.copy(
                errorMessage = message
            )
    }
}