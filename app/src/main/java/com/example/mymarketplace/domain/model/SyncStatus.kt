package com.example.mymarketplace.domain.model

enum class SyncStatus {
    SYNCED,
    PENDING_CREATE,
    PENDING_UPDATE,
    CONFLICT
}
