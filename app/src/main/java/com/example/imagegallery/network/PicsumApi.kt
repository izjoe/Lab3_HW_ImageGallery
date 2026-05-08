package com.example.imagegallery.network

import com.example.imagegallery.model.ImageItem
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {
    @GET("v2/list")
    suspend fun getImages(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<ImageItem>

    companion object {
        const val BASE_URL = "https://picsum.photos/"
    }
}
