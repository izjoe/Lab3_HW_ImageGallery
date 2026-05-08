package com.example.imagegallery.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.imagegallery.model.ProcessedImage
import com.example.imagegallery.model.SearchHistory

@Database(entities = [ProcessedImage::class, SearchHistory::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun searchDao(): SearchDao
}
