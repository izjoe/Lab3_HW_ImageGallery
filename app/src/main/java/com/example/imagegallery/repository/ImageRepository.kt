package com.example.imagegallery.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.imagegallery.cache.ImageDao
import com.example.imagegallery.cache.SearchDao
import com.example.imagegallery.data.PicsumPagingSource
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.model.ProcessedImage
import com.example.imagegallery.model.SearchHistory
import com.example.imagegallery.network.PicsumApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val api: PicsumApi,
    private val imageDao: ImageDao,
    private val searchDao: SearchDao
) {
    fun getSearchResults(query: String): Flow<PagingData<ImageItem>> {
        // Lưu ý: Picsum không hỗ trợ search thực sự theo query. 
        // Ở đây ta trả về danh sách ảnh v2/list mặc định.
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PicsumPagingSource(api) }
        ).flow
    }

    suspend fun getProcessedImage(id: String): ProcessedImage? {
        return imageDao.getProcessedImage(id)
    }

    suspend fun saveProcessedImage(image: ProcessedImage) {
        imageDao.insertProcessedImage(image)
    }

    suspend fun saveSearchQuery(query: String) {
        if (query.isNotBlank()) {
            searchDao.insertSearch(SearchHistory(query))
        }
    }

    fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchDao.getRecentSearches()
    }

    fun getSearchSuggestions(query: String): Flow<List<SearchHistory>> {
        return searchDao.getSuggestions(query)
    }
}
