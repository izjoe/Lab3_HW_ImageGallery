package com.example.imagegallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imagegallery.BuildConfig
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.network.PixabayApi

class PixabayPagingSource(
    private val api: PixabayApi,
    private val query: String
) : PagingSource<Int, ImageItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val position = params.key ?: 1
        return try {
            val response = api.searchImages(
                apiKey = BuildConfig.PIXABAY_API_KEY,
                query = query,
                page = position,
                perPage = params.loadSize
            )
            val repos = response.hits

            LoadResult.Page(
                data = repos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (repos.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
