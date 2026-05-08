package com.example.mymarketplace.domain.model

data class CreateListingInput(
    val title: String,
    val description: String,
    val price: Double,
    val localImagePath: String?
)