package com.esentiele.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.esentiele.app.domain.model.Outfit

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey val id: String,
    val itemIds: String,
    val occasion: String,
    val rating: Int?,
    val aiScore: Int?,
    val aiFeedback: String?,
    val createdAt: Long,
    val scheduledDate: Long? = null,
    val isCapsule: Boolean = false
) {
    fun toModel(): Outfit {
        return Outfit(
            id = id,
            itemIds = if (itemIds.isEmpty()) emptyList() else itemIds.split(","),
            occasion = occasion,
            rating = rating,
            aiScore = aiScore,
            aiFeedback = aiFeedback,
            createdAt = createdAt,
            scheduledDate = scheduledDate,
            isCapsule = isCapsule
        )
    }

    companion object {
        fun fromModel(model: Outfit): OutfitEntity {
            return OutfitEntity(
                id = model.id,
                itemIds = model.itemIds.joinToString(","),
                occasion = model.occasion,
                rating = model.rating,
                aiScore = model.aiScore,
                aiFeedback = model.aiFeedback,
                createdAt = model.createdAt,
                scheduledDate = model.scheduledDate,
                isCapsule = model.isCapsule
            )
        }
    }
}
