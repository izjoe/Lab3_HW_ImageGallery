package com.example.imagegallery.cache

import androidx.room.TypeConverter
import com.example.imagegallery.model.DetectionResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromDetectionResultList(value: List<DetectionResult>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toDetectionResultList(value: String): List<DetectionResult> {
        val listType = object : TypeToken<List<DetectionResult>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
