package com.example.mymarketplace.util

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>
    fun isOnline(): Boolean

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}
