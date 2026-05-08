package com.example.mymarketplace.data.remote.dto

data class ListingDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val isFavorite: Boolean,
    val updatedAt: Long
)
