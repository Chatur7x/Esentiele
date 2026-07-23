package com.esentiele.app.data.repository

import android.graphics.Bitmap
import com.esentiele.app.data.local.ClothingDao
import com.esentiele.app.data.local.ClothingItemEntity
import com.esentiele.app.data.local.OutfitDao
import com.esentiele.app.data.local.OutfitEntity
import com.esentiele.app.data.remote.BattleResult
import com.esentiele.app.data.remote.GarmentAnalysis
import com.esentiele.app.data.remote.LocalStylingEngine
import com.esentiele.app.data.remote.OutfitCritique
import com.esentiele.app.domain.model.ClothingItem
import com.esentiele.app.domain.model.Outfit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WardrobeRepository(
    private val clothingDao: ClothingDao,
    private val outfitDao: OutfitDao,
    private val localStylingEngine: LocalStylingEngine
) {

    // Clothing Item Operations
    fun getAllClothingItems(): Flow<List<ClothingItem>> {
        return clothingDao.getAll().map { list -> list.map { it.toModel() } }
    }

    fun getClothingItemsByCategory(category: String): Flow<List<ClothingItem>> {
        return clothingDao.getByCategory(category).map { list -> list.map { it.toModel() } }
    }

    suspend fun getClothingItemById(id: String): ClothingItem? {
        return clothingDao.getById(id)?.toModel()
    }

    fun getItemCount(): Flow<Int> {
        return clothingDao.getItemCount()
    }

    suspend fun insertClothingItem(item: ClothingItem) {
        clothingDao.insert(ClothingItemEntity.fromModel(item))
    }

    suspend fun deleteClothingItem(item: ClothingItem) {
        clothingDao.delete(ClothingItemEntity.fromModel(item))
    }

    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) {
        clothingDao.updateFavorite(id, isFavorite)
    }

    suspend fun updateWearCount(id: String, timesWorn: Int, lastWorn: Long) {
        clothingDao.updateWearCount(id, timesWorn, lastWorn)
    }

    suspend fun updateNfcTag(id: String, nfcTagId: String) {
        clothingDao.updateNfcTag(id, nfcTagId)
    }

    suspend fun getItemByNfcTagId(nfcTagId: String): ClothingItem? {
        return clothingDao.getByNfcTagId(nfcTagId)?.toModel()
    }

    fun getTaggedItems(): Flow<List<ClothingItem>> {
        return clothingDao.getTaggedItems().map { list -> list.map { it.toModel() } }
    }

    fun getMostWorn(limit: Int = 10): Flow<List<ClothingItem>> {
        return clothingDao.getMostWorn(limit).map { list -> list.map { it.toModel() } }
    }

    fun getForgottenItems(thresholdMs: Long): Flow<List<ClothingItem>> {
        return clothingDao.getForgottenItems(thresholdMs).map { list -> list.map { it.toModel() } }
    }

    // Outfit Operations
    fun getAllOutfits(): Flow<List<Outfit>> {
        return outfitDao.getAll().map { list -> list.map { it.toModel() } }
    }

    suspend fun getOutfitById(id: String): Outfit? {
        return outfitDao.getById(id)?.toModel()
    }

    suspend fun insertOutfit(outfit: Outfit) {
        outfitDao.insert(OutfitEntity.fromModel(outfit))
    }

    suspend fun deleteOutfit(outfit: Outfit) {
        outfitDao.delete(OutfitEntity.fromModel(outfit))
    }

    suspend fun updateOutfitRating(id: String, rating: Int) {
        outfitDao.updateRating(id, rating)
    }

    // AI Operations
    suspend fun analyzeGarment(imageBitmap: Bitmap): Result<GarmentAnalysis> {
        return localStylingEngine.analyzeClothing(imageBitmap)
    }

    suspend fun suggestOutfit(wardrobeDescription: String, weather: String, occasion: String): Result<String> {
        return localStylingEngine.generateOutfitSuggestion(wardrobeDescription, weather, occasion)
    }

    suspend fun critiqueOutfit(imageBitmap: Bitmap): Result<OutfitCritique> {
        return localStylingEngine.critiqueOutfit(imageBitmap)
    }

    suspend fun roastBattle(outfit1Desc: String, outfit2Desc: String): Result<BattleResult> {
        return localStylingEngine.roastBattle(outfit1Desc, outfit2Desc)
    }

    fun generateRealOutfit(items: List<ClothingItem>, weather: String, occasion: String): LocalStylingEngine.OutfitCombination? {
        return localStylingEngine.generateRealOutfitFromWardrobe(items, weather, occasion)
    }
}
