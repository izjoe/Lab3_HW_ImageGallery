package com.example.imagegallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.network.PicsumApi

class PicsumPagingSource(
    private val api: PicsumApi
) : PagingSource<Int, ImageItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val position = params.key ?: 1
        return try {
            val response = api.getImages(
                page = position,
                limit = params.loadSize
            )

            LoadResult.Page(
                data = response,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
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
