package com.example.imagegallery.model

import com.google.gson.annotations.SerializedName

data class ImageItem(
    @SerializedName("id") val id: String,
    @SerializedName(value = "author", alternate = ["user"]) val user: String,
    @SerializedName(value = "url", alternate = ["pageURL"]) val pageURL: String,
    @SerializedName(value = "download_url", alternate = ["largeImageURL"]) val largeImageURL: String,
    @SerializedName("tags") private val _tags: String? = null,
    @SerializedName("previewURL") private val _previewURL: String? = null,
    @SerializedName("webformatURL") private val _webformatURL: String? = null
) {
    val tags: String 
        get() = _tags ?: "Photo by $user"
    
    val previewURL: String 
        get() = _previewURL ?: "https://picsum.photos/id/$id/400/300"
    
    val webformatURL: String 
        get() = _webformatURL ?: "https://picsum.photos/id/$id/800/600"

    // Helper properties for UI consistency
    val thumbnailUrl: String
        get() = _previewURL ?: "https://picsum.photos/id/$id/400/400"
    
    val displayUrl: String
        get() = largeImageURL
}
