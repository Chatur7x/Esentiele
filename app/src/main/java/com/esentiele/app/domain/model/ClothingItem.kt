package com.esentiele.app.domain.model

data class ClothingItem(
    val id: String,
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
)
