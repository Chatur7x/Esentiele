package com.esentiele.app.domain.model

data class Outfit(
    val id: String,
    val itemIds: List<String>,
    val occasion: String,
    val rating: Int?,
    val aiScore: Int?,
    val aiFeedback: String?,
    val createdAt: Long,
    val scheduledDate: Long? = null,
    val isCapsule: Boolean = false
)
