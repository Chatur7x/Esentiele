package com.esentiele.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits ORDER BY createdAt DESC")
    fun getAll(): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE id = :id")
    suspend fun getById(id: String): OutfitEntity?

    @Query("SELECT * FROM outfits WHERE scheduledDate IS NOT NULL ORDER BY scheduledDate ASC")
    fun getScheduledOutfits(): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE isCapsule = 1 ORDER BY createdAt DESC")
    fun getCapsuleOutfits(): Flow<List<OutfitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(outfit: OutfitEntity)

    @Delete
    suspend fun delete(outfit: OutfitEntity)

    @Query("UPDATE outfits SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: String, rating: Int)

    @Query("UPDATE outfits SET scheduledDate = :scheduledDate WHERE id = :id")
    suspend fun updateScheduledDate(id: String, scheduledDate: Long?)
}
