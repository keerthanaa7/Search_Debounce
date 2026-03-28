package com.example.searchwithdebounceandcancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Multi-Image Upload / Progress: Manage independent, concurrent progress states.
data class UploadItem(
    val id: String,
    val fileName: String,
    val progress: Float = 0f, // 0.0 to 1.0 for Compose ProgressIndicator
    val state: UploadState = UploadState.IDLE
)

enum class UploadState {
    IDLE, UPLOADING, COMPLETED, ERROR
}

class ImageUploadViewModel: ViewModel() {
        private val _uploads = MutableStateFlow<List<UploadItem>>(emptyList())
        val uploads = _uploads.asStateFlow()

        fun startUploads(fileNames: List<String>) {
            // 1. Initialize the list
            val newList = fileNames.map {
                UploadItem(id = java.util.UUID.randomUUID().toString(), fileName = it)
            }
            _uploads.value = newList

            // 2. Launch independent coroutines for each "file"
            newList.forEach { item ->
                viewModelScope.launch {
                    simulateFileUpload(item.id)
                }
            }
        }

        private suspend fun simulateFileUpload(itemId: String) {
            var currentProgress = 0f

            // Update state to UPLOADING
            updateItem(itemId) { it.copy(state = UploadState.UPLOADING) }

            while (currentProgress < 1f) {
                delay((300..1000).random().toLong()) // Random latency
                currentProgress += 0.1f

                if (currentProgress > 1f) currentProgress = 1f

                // The "Trap" Fix: Update only this specific item
                updateItem(itemId) { it.copy(progress = currentProgress) }
            }

            // Mark as COMPLETED
            updateItem(itemId) { it.copy(state = UploadState.COMPLETED, progress = 1f) }
        }

        private fun updateItem(id: String, transform: (UploadItem) -> UploadItem) {
            _uploads.value = _uploads.value.map { item ->
                if (item.id == id) transform(item) else item
            }
        }
    }
