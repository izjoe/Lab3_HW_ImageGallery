package com.example.imagegallery.network

import com.example.imagegallery.model.PixabayResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {
    @GET("api/")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("image_type") imageType: String = "photo"
    ): PixabayResponse

    companion object {
        const val BASE_URL = "https://pixabay.com/"
    }
}
