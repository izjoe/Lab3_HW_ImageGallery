package com.example.imagegallery.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.detection.ObjectDetectorHelper
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.model.ProcessedImage
import com.example.imagegallery.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val objectDetectorHelper: ObjectDetectorHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun processImage(imageItem: ImageItem, bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            
            // Check AI Cache
            val cached = repository.getProcessedImage(imageItem.id)
            if (cached != null) {
                _uiState.value = DetailUiState.Success(cached)
                return@launch
            }

            // Run Object Detection
            val detections = objectDetectorHelper.detect(bitmap)
            val processedImage = ProcessedImage(
                id = imageItem.id,
                imageUrl = imageItem.largeImageURL,
                detections = detections
            )

            // Save to Cache
            repository.saveProcessedImage(processedImage)
            
            _uiState.value = DetailUiState.Success(processedImage)
        }
    }
}

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val processedImage: ProcessedImage) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
