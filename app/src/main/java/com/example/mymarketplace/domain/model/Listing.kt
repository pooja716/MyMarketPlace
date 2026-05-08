package com.example.mymarketplace.domain.model

data class Listing(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val localImagePath: String?,
    val isFavorite: Boolean,
    val syncStatus: SyncStatus,
    val updatedAt: Long
)
