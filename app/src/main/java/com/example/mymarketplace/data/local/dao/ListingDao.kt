package com.example.mymarketplace.data.local.dao

import androidx.room.*
import com.example.mymarketplace.data.local.entity.ListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings ORDER BY updatedAt DESC")
    fun observeListings(): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getListingById(id: String): ListingEntity?

    @Query("SELECT * FROM listings WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingListings(): List<ListingEntity>

    @Query("SELECT * FROM listings")
    suspend fun getAllListings(): List<ListingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ListingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listings: List<ListingEntity>)

    @Update
    suspend fun updateListing(listing: ListingEntity)

    @Query("UPDATE listings SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavoriteById(id: String)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListing(id: String)
}
