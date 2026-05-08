package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.model.CreateListingInput
import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.repository.ListingRepository
import javax.inject.Inject

class CreateListingUseCase @Inject constructor(
    private val repository: ListingRepository
) {

        suspend operator fun invoke(input: CreateListingInput): Result<Listing> {
            validateInput(input)?.let { errorMessage ->
                return Result.failure(IllegalArgumentException(errorMessage))
            }

            return repository.createListing(input)
        }

        private fun validateInput(input: CreateListingInput): String? {
            if (input.title.isBlank()) return "Title cannot be empty"
            if (input.title.trim().length < 3) return "Title must be at least 3 characters"
            if (input.description.isBlank()) return "Description cannot be empty"
            if (input.price <= 0) return "Price must be greater than 0"
            return null
        }
}
