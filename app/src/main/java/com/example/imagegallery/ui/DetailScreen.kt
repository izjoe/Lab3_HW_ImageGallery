package com.example.imagegallery.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.model.ProcessedImage
import com.example.imagegallery.viewmodel.DetailViewModel
import com.example.imagegallery.viewmodel.DetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    imageItem: ImageItem,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(imageItem) {
        val loader = context.imageLoader
        val request = ImageRequest.Builder(context)
            .data(imageItem.largeImageURL)
            .allowHardware(false)
            .build()
        
        val result = (loader.execute(request) as? SuccessResult)?.drawable
        val bitmap = (result as? BitmapDrawable)?.bitmap
        if (bitmap != null) {
            viewModel.processImage(imageItem, bitmap)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Analysis") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            AsyncImage(
                                model = imageItem.largeImageURL,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            
                            DetectionOverlay(processedImage = state.processedImage)
                        }
                        
                        Text(
                            text = "Detected Objects:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        
                        LazyColumn(modifier = Modifier.weight(0.5f).padding(horizontal = 16.dp)) {
                            items(state.processedImage.detections) { detection ->
                                ListItem(
                                    headlineContent = { Text(detection.label) },
                                    trailingContent = { Text("${(detection.score * 100).toInt()}%") }
                                )
                            }
                        }
                    }
                }
                is DetailUiState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DetectionOverlay(processedImage: ProcessedImage) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        processedImage.detections.forEach { detection ->
            detection.boundingBox?.let { box ->
                val rect = androidx.compose.ui.geometry.Rect(
                    left = box.left * size.width,
                    top = box.top * size.height,
                    right = box.right * size.width,
                    bottom = box.bottom * size.height
                )
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(rect.left, rect.top),
                    size = Size(rect.width, rect.height),
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}
