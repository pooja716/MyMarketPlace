package com.example.mymarketplace.data.remote.api

import com.example.mymarketplace.data.remote.dto.*
import retrofit2.http.*
import okhttp3.MultipartBody

interface MarketplaceApi {
    @GET("listings")
    suspend fun getListings(): ListingsResponse

    @POST("listings")
    suspend fun createListing(@Body dto: CreateListingDto): ListingDto

    @PUT("listings/{id}")
    suspend fun updateListing(@Path("id") id: String, @Body dto: UpdateListingDto): ListingDto

    @DELETE("listings/{id}")
    suspend fun deleteListing(@Path("id") id: String)

    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): ImageUploadResponse
}
