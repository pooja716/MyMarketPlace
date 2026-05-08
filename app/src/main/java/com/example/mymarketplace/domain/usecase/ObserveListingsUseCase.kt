package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.repository.ListingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveListingsUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    operator fun invoke(): Flow<List<Listing>> {
        return repository.observeListings()
    }
}
