package com.example.imagegallery.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.imagegallery.model.ProcessedImage
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM processed_images WHERE id = :id")
    suspend fun getProcessedImage(id: String): ProcessedImage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessedImage(image: ProcessedImage)

    @Query("SELECT * FROM processed_images")
    fun getAllProcessedImages(): Flow<List<ProcessedImage>>
}
