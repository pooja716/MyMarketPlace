package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.repository.ListingRepository
import javax.inject.Inject

class GetListingByIdUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(id: String): Listing? = repository.getListingById(id)
}
