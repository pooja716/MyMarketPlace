package com.example.mymarketplace.data.remote.dto

data class CreateListingDto(
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?
)
