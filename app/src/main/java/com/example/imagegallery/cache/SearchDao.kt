package com.example.imagegallery.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.imagegallery.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistory)

    @Query("SELECT * FROM search_history WHERE `query` LIKE :query || '%' ORDER BY timestamp DESC")
    fun getSuggestions(query: String): Flow<List<SearchHistory>>
}
