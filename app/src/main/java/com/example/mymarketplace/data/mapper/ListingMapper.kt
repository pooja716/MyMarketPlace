package com.example.mymarketplace.data.mapper

import com.example.mymarketplace.data.local.entity.ListingEntity
import com.example.mymarketplace.data.remote.dto.ListingDto
import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.model.PendingAction
import com.example.mymarketplace.domain.model.SyncStatus

fun ListingEntity.toDomain(): Listing = Listing(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    localImagePath = localImagePath,
    isFavorite = isFavorite,
    syncStatus = syncStatus,
    updatedAt = updatedAt
)

fun Listing.toEntity(): ListingEntity = ListingEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    localImagePath = localImagePath,
    isFavorite = isFavorite,
    syncStatus = syncStatus,
    pendingAction = null,
    updatedAt = updatedAt
)

fun ListingDto.toEntity(existing: ListingEntity? = null): ListingEntity = ListingEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    localImagePath = existing?.localImagePath,
    isFavorite = existing?.isFavorite ?: isFavorite,
    syncStatus = SyncStatus.SYNCED,
    pendingAction = null,
    updatedAt = updatedAt
)

fun ListingDto.toSyncedEntity(existing: ListingEntity): ListingEntity = ListingEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    localImagePath = existing.localImagePath,
    isFavorite = existing.isFavorite,
    syncStatus = SyncStatus.SYNCED,
    pendingAction = null,
    updatedAt = updatedAt
)
