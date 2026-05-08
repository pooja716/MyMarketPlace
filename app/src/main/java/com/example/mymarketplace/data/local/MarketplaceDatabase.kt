package com.example.mymarketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mymarketplace.data.local.dao.ListingDao
import com.example.mymarketplace.data.local.entity.ListingEntity
import com.example.mymarketplace.data.local.converter.Converters

@Database(
    entities = [ListingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract val listingDao: ListingDao
}
