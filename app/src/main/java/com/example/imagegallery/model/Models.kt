package com.example.imagegallery.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "processed_images")
data class ProcessedImage(
    @PrimaryKey val id: String,
    val imageUrl: String,
    val detections: List<DetectionResult>,
    val timestamp: Long = System.currentTimeMillis()
)

data class DetectionResult(
    val label: String,
    val score: Float,
    val boundingBox: RectF? = null
)

data class RectF(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
