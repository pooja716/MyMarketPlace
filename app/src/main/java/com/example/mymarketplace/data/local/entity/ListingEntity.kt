package com.example.mymarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymarketplace.domain.model.SyncStatus
import com.example.mymarketplace.domain.model.PendingAction

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val localImagePath: String?,
    val isFavorite: Boolean,
    val syncStatus: SyncStatus,
    val pendingAction: PendingAction?,
    val updatedAt: Long
)
