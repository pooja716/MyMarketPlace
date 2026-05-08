package com.example.mymarketplace.di

import com.example.mymarketplace.data.repository.ListingRepositoryImpl
import com.example.mymarketplace.data.scheduler.SyncSchedulerImpl
import com.example.mymarketplace.domain.repository.ListingRepository
import com.example.mymarketplace.domain.scheduler.SyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindListingRepository(
        listingRepositoryImpl: ListingRepositoryImpl
    ): ListingRepository

    @Binds
    @Singleton
    abstract fun bindSyncScheduler(
        syncSchedulerImpl: SyncSchedulerImpl
    ): SyncScheduler
}
