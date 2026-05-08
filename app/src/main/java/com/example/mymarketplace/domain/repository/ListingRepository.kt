package com.example.mymarketplace.domain.repository

import com.example.mymarketplace.domain.model.CreateListingInput
import com.example.mymarketplace.domain.model.Listing
import kotlinx.coroutines.flow.Flow

interface ListingRepository {
    fun observeListings(): Flow<List<Listing>>
    suspend fun getListingById(id: String): Listing?
    suspend fun createListing(input: CreateListingInput): Result<Listing>
    suspend fun insertListing(listing: Listing)
    suspend fun updateListing(listing: Listing)
    suspend fun deleteListing(id: String)
    suspend fun toggleFavorite(id: String)
    suspend fun refreshListings()
    suspend fun syncPendingListings()
}
