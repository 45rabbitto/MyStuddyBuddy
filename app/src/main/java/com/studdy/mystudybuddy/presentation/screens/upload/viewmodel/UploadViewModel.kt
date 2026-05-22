package com.studdy.mystuddybuddy.presentation.screens.upload.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.studdy.mystuddybuddy.presentation.screens.upload.model.UploadFile
import com.studdy.mystuddybuddy.presentation.screens.upload.state.UploadUiState

class UploadViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            UploadUiState()
        )

    val uiState: StateFlow<UploadUiState> =
        _uiState.asStateFlow()

    fun addFile(file: UploadFile){

        val updatedList =
            _uiState.value.uploadedFiles + file

        _uiState.value =
            _uiState.value.copy(
                uploadedFiles = updatedList,
                uploadSuccess = true
            )
    }

    fun removeFile(file: UploadFile){

        val updatedList =
            _uiState.value.uploadedFiles.filter {
                it != file
            }

        _uiState.value =
            _uiState.value.copy(
                uploadedFiles = updatedList
            )
    }

    fun setLoading(
        loading: Boolean
    ){

        _uiState.value =
            _uiState.value.copy(
                isLoading = loading
            )
    }

    fun setError(
        message: String?
    ){

        _uiState.value =
            _uiState.value.copy(
                errorMessage = message
            )
    }
}