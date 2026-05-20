package com.studdy.mystuddybuddy.presentation.screens.upload.state

import com.studdy.mystuddybuddy.presentation.screens.upload.model.UploadFile

data class UploadUiState(

    // status loading upload
    val isLoading: Boolean = false,

    // daftar file yang sudah diupload
    val uploadedFiles: List<UploadFile> = emptyList(),

    // file yang sedang dipilih
    val selectedFile: UploadFile? = null,

    // status upload berhasil
    val uploadSuccess: Boolean = false,

    // pesan error
    val errorMessage: String? = null
)