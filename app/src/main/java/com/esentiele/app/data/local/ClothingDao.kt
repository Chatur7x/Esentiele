package com.esentiele.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {
    @Query("SELECT * FROM clothing_items ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE category = :category ORDER BY dateAdded DESC")
    fun getByCategory(category: String): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getById(id: String): ClothingItemEntity?

    @Query("SELECT COUNT(*) FROM clothing_items")
    fun getItemCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClothingItemEntity)

    @Delete
    suspend fun delete(item: ClothingItemEntity)

    @Query("UPDATE clothing_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE clothing_items SET timesWorn = :timesWorn, lastWorn = :lastWorn WHERE id = :id")
    suspend fun updateWearCount(id: String, timesWorn: Int, lastWorn: Long)

    @Query("UPDATE clothing_items SET nfcTagId = :nfcTagId WHERE id = :id")
    suspend fun updateNfcTag(id: String, nfcTagId: String)

    @Query("SELECT * FROM clothing_items WHERE nfcTagId = :nfcTagId LIMIT 1")
    suspend fun getByNfcTagId(nfcTagId: String): ClothingItemEntity?

    @Query("SELECT * FROM clothing_items WHERE nfcTagId != '' ORDER BY lastWorn DESC")
    fun getTaggedItems(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items ORDER BY timesWorn DESC LIMIT :limit")
    fun getMostWorn(limit: Int): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE lastWorn = 0 OR lastWorn < :thresholdMs ORDER BY dateAdded DESC")
    fun getForgottenItems(thresholdMs: Long): Flow<List<ClothingItemEntity>>
}
