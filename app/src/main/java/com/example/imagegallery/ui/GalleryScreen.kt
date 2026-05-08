package com.example.imagegallery.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onImageClick: (ImageItem) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val pagedImages = viewModel.images.collectAsLazyPagingItems()
    var active by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            query = searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            onSearch = {
                viewModel.onSearchClicked(it)
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Search images...") },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = null
                ) 
            }
        ) {
            val history by viewModel.searchHistory.collectAsState()
            history.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.query) },
                    modifier = Modifier.clickable {
                        viewModel.onSearchClicked(item.query)
                        active = false
                    }
                )
            }
        }

        when {
            // Trạng thái tải lần đầu
            pagedImages.loadState.refresh is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            // Trạng thái lỗi khi tải lần đầu
            pagedImages.loadState.refresh is LoadState.Error -> {
                val error = pagedImages.loadState.refresh as LoadState.Error
                ErrorMessage(
                    message = error.error.localizedMessage ?: "Unknown error",
                    onRetry = { pagedImages.retry() }
                )
            }
            // Trạng thái danh sách trống sau khi tải xong
            pagedImages.itemCount == 0 && pagedImages.loadState.append is LoadState.NotLoading && pagedImages.loadState.append.endOfPaginationReached -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No images found.", style = MaterialTheme.typography.bodyLarge)
                }
            }
            // Hiển thị danh sách hình ảnh
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pagedImages.itemCount) { index ->
                        val image = pagedImages[index]
                        if (image != null) {
                            ImageCard(image = image, onClick = { onImageClick(image) })
                        }
                    }

                    // Tải thêm (Pagination)
                    if (pagedImages.loadState.append is LoadState.Loading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning, 
            contentDescription = null, 
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun ImageCard(image: ImageItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = image.webformatURL,
            contentDescription = image.tags,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
