package com.example.imagegallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.model.SearchHistory
import com.example.imagegallery.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("nature") // Khởi tạo với từ khóa mặc định
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val images: Flow<PagingData<ImageItem>> = _searchQuery
        .debounce(300)
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            repository.getSearchResults(query)
        }
        .cachedIn(viewModelScope)

    val searchHistory: StateFlow<List<SearchHistory>> = repository.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun onSearchClicked(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.saveSearchQuery(query)
        }
    }

    fun getSuggestions(query: String): Flow<List<SearchHistory>> {
        return repository.getSearchSuggestions(query)
    }
}
