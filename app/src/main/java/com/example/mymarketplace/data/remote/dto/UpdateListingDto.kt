package com.example.mymarketplace.data.remote.dto

data class UpdateListingDto(
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val isFavorite: Boolean
)
