package com.example.mymarketplace.data.repository

import com.example.mymarketplace.data.local.dao.ListingDao
import com.example.mymarketplace.data.local.entity.ListingEntity
import com.example.mymarketplace.data.mapper.toDomain
import com.example.mymarketplace.data.mapper.toEntity
import com.example.mymarketplace.data.mapper.toSyncedEntity
import com.example.mymarketplace.data.remote.api.MarketplaceApi
import com.example.mymarketplace.data.remote.dto.CreateListingDto
import com.example.mymarketplace.data.remote.dto.UpdateListingDto
import com.example.mymarketplace.domain.model.CreateListingInput
import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.model.PendingAction
import com.example.mymarketplace.domain.model.SyncStatus
import com.example.mymarketplace.domain.repository.ListingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ListingRepositoryImpl @Inject constructor(
    private val dao: ListingDao,
    private val api: MarketplaceApi
) : ListingRepository {

    override fun observeListings(): Flow<List<Listing>> {
        return dao.observeListings().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getListingById(id: String): Listing? {
        return dao.getListingById(id)?.toDomain()
    }

    override suspend fun createListing(input: CreateListingInput): Result<Listing> {
        return try {
            val entity = ListingEntity(
                id = UUID.randomUUID().toString(),
                title = input.title,
                description = input.description,
                price = input.price,
                imageUrl = null,
                localImagePath = input.localImagePath,
                isFavorite = false,
                syncStatus = SyncStatus.PENDING_CREATE,
                pendingAction = PendingAction.CREATE,
                updatedAt = System.currentTimeMillis()
            )
            dao.insertListing(entity)
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertListing(listing: Listing) {
        dao.insertListing(
            listing.toEntity().copy(
                syncStatus = SyncStatus.PENDING_CREATE,
                pendingAction = PendingAction.CREATE
            )
        )
    }

    override suspend fun updateListing(listing: Listing) {
        dao.insertListing(
            listing.toEntity().copy(
                syncStatus = SyncStatus.PENDING_UPDATE,
                pendingAction = PendingAction.UPDATE
            )
        )
    }

    override suspend fun deleteListing(id: String) {
        dao.deleteListing(id)
    }

    override suspend fun toggleFavorite(id: String) {
        dao.toggleFavoriteById(id)
    }

    override suspend fun refreshListings() {
        try {
            val response = api.getListings()
            val existingMap = dao.getAllListings().associateBy { it.id }
            val entities = response.listings.mapNotNull { dto ->
                val existing = existingMap[dto.id]
                if (existing != null && existing.syncStatus != SyncStatus.SYNCED) return@mapNotNull null
                dto.toEntity(existing)
            }
            dao.insertAll(entities)
        } catch (_: Exception) { }
    }

    override suspend fun syncPendingListings() {
        val pending = dao.getPendingListings()
        pending.forEach { entity ->
            try {
                val response = when (entity.pendingAction) {
                    PendingAction.CREATE -> api.createListing(
                        CreateListingDto(entity.title, entity.description, entity.price, entity.imageUrl)
                    )
                    PendingAction.UPDATE -> api.updateListing(
                        entity.id,
                        UpdateListingDto(entity.title, entity.description, entity.price, entity.imageUrl, entity.isFavorite)
                    )
                    null -> null
                }
                response?.let { dto ->
                    dao.insertListing(dto.toSyncedEntity(entity))
                    if (dto.id != entity.id) dao.deleteListing(entity.id)
                }
            } catch (_: Exception) { }
        }
    }
}
