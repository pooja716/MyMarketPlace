package com.example.mymarketplace.data.repository

import com.example.mymarketplace.data.local.dao.ListingDao
import com.example.mymarketplace.data.local.entity.ListingEntity
import com.example.mymarketplace.data.remote.api.MarketplaceApi
import com.example.mymarketplace.data.remote.dto.ListingDto
import com.example.mymarketplace.data.remote.dto.ListingsResponse
import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.model.PendingAction
import com.example.mymarketplace.domain.model.SyncStatus
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ListingRepositoryTest {

    private lateinit var dao: ListingDao
    private lateinit var api: MarketplaceApi
    private lateinit var repository: ListingRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        api = mockk()
        repository = ListingRepositoryImpl(dao, api)
    }

    @Test
    fun `syncPending creates listing on API and marks as SYNCED`() = runTest {
        val pendingListing = ListingEntity(
            id = "1", title = "Test", description = "Desc", price = 10.0,
            imageUrl = null, localImagePath = null, isFavorite = false,
            syncStatus = SyncStatus.PENDING_CREATE, pendingAction = PendingAction.CREATE,
            updatedAt = 100L
        )
        val apiResponse = ListingDto(
            id = "1", title = "Test", description = "Desc", price = 10.0,
            imageUrl = null, isFavorite = false, updatedAt = 200L
        )

        coEvery { dao.getPendingListings() } returns listOf(pendingListing)
        coEvery { api.createListing(any()) } returns apiResponse

        repository.syncPendingListings()

        coVerify { api.createListing(any()) }
        coVerify { dao.insertListing(match { it.syncStatus == SyncStatus.SYNCED }) }
    }

    @Test
    fun `syncPending applies last-write-wins on conflict`() = runTest {
        val pendingListing = ListingEntity(
            id = "1", title = "Local Title", description = "Desc", price = 10.0,
            imageUrl = null, localImagePath = null, isFavorite = false,
            syncStatus = SyncStatus.PENDING_UPDATE, pendingAction = PendingAction.UPDATE,
            updatedAt = 100L
        )
        val apiResponse = ListingDto(
            id = "1", title = "Server Title", description = "Desc", price = 10.0,
            imageUrl = null, isFavorite = false, updatedAt = 300L
        )

        coEvery { dao.getPendingListings() } returns listOf(pendingListing)
        coEvery { api.updateListing(any(), any()) } returns apiResponse

        repository.syncPendingListings()

        coVerify { dao.insertListing(match { it.title == "Server Title" && it.updatedAt == 300L }) }
    }

    @Test
    fun `createListing when offline saves locally with PENDING_CREATE status`() = runTest {
        val newListing = Listing(
            id = "1", title = "New", description = "Desc", price = 50.0,
            imageUrl = null, localImagePath = null, isFavorite = false,
            syncStatus = SyncStatus.SYNCED, updatedAt = 100L
        )

        repository.insertListing(newListing)

        coVerify { dao.insertListing(match { it.syncStatus == SyncStatus.PENDING_CREATE }) }
        confirmVerified(api)
    }

    @Test
    fun `syncPending deletes old local UUID row when server assigns a new ID`() = runTest {
        val localId = "local-uuid-abc"
        val serverId = "server-assigned-id-123"

        val pendingListing = ListingEntity(
            id = localId, title = "My Item", description = "Desc", price = 25.0,
            imageUrl = null, localImagePath = null, isFavorite = false,
            syncStatus = SyncStatus.PENDING_CREATE, pendingAction = PendingAction.CREATE,
            updatedAt = 100L
        )
        val apiResponse = ListingDto(
            id = serverId, title = "My Item", description = "Desc", price = 25.0,
            imageUrl = null, isFavorite = false, updatedAt = 200L
        )

        coEvery { dao.getPendingListings() } returns listOf(pendingListing)
        coEvery { api.createListing(any()) } returns apiResponse

        repository.syncPendingListings()

        coVerify { dao.insertListing(match { it.id == serverId && it.syncStatus == SyncStatus.SYNCED }) }
        coVerify { dao.deleteListing(localId) }
    }

    @Test
    fun `toggleFavorite calls atomic SQL update on DAO`() = runTest {
        repository.toggleFavorite("item-42")

        coVerify { dao.toggleFavoriteById("item-42") }
        coVerify(exactly = 0) { dao.getListingById(any()) }
        coVerify(exactly = 0) { dao.insertListing(any()) }
    }

    @Test
    fun `refreshListings preserves isFavorite for already-synced items`() = runTest {
        val existingEntity = ListingEntity(
            id = "id_5", title = "Listing #5", description = "Desc", price = 15.0,
            imageUrl = "https://picsum.photos/seed/5/320/240", localImagePath = null,
            isFavorite = true,
            syncStatus = SyncStatus.SYNCED, pendingAction = null, updatedAt = 1000L
        )
        val remoteDto = ListingDto(
            id = "id_5", title = "Listing #5", description = "Desc", price = 15.0,
            imageUrl = "https://picsum.photos/seed/5/320/240",
            isFavorite = false,
            updatedAt = 1000L
        )

        coEvery { api.getListings() } returns ListingsResponse(listOf(remoteDto))
        coEvery { dao.getAllListings() } returns listOf(existingEntity)

        repository.refreshListings()

        coVerify {
            dao.insertAll(match { entities ->
                entities.any { it.id == "id_5" && it.isFavorite }
            })
        }
    }
}
