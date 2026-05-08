package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.repository.ListingRepository
import javax.inject.Inject

class SyncPendingListingsUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke() {
        repository.syncPendingListings()
    }
}
