package com.example.mymarketplace.di

import android.content.Context
import androidx.room.Room
import com.example.mymarketplace.data.local.MarketplaceDatabase
import com.example.mymarketplace.data.local.dao.ListingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MarketplaceDatabase {
        return Room.databaseBuilder(
            context,
            MarketplaceDatabase::class.java,
            "marketplace_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideListingDao(db: MarketplaceDatabase): ListingDao {
        return db.listingDao
    }
}
