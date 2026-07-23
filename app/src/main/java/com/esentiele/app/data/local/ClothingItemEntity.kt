package com.esentiele.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.esentiele.app.domain.model.ClothingItem

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey val id: String,
    val localImageUri: String,
    val category: String,
    val subCategory: String,
    val primaryColor: String,
    val secondaryColor: String?,
    val material: String,
    val pattern: String,
    val season: String,
    val formality: String,
    val dateAdded: Long,
    val isFavorite: Boolean,
    val price: Double = 0.0,
    val timesWorn: Int = 0,
    val lastWorn: Long = 0L,
    val nfcTagId: String = ""
) {
    fun toModel(): ClothingItem {
        return ClothingItem(
            id = id,
            localImageUri = localImageUri,
            category = category,
            subCategory = subCategory,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            material = material,
            pattern = pattern,
            season = season,
            formality = formality,
            dateAdded = dateAdded,
            isFavorite = isFavorite,
            price = price,
            timesWorn = timesWorn,
            lastWorn = lastWorn,
            nfcTagId = nfcTagId
        )
    }

    companion object {
        fun fromModel(model: ClothingItem): ClothingItemEntity {
            return ClothingItemEntity(
                id = model.id,
                localImageUri = model.localImageUri,
                category = model.category,
                subCategory = model.subCategory,
                primaryColor = model.primaryColor,
                secondaryColor = model.secondaryColor,
                material = model.material,
                pattern = model.pattern,
                season = model.season,
                formality = model.formality,
                dateAdded = model.dateAdded,
                isFavorite = model.isFavorite,
                price = model.price,
                timesWorn = model.timesWorn,
                lastWorn = model.lastWorn,
                nfcTagId = model.nfcTagId
            )
        }
    }
}
