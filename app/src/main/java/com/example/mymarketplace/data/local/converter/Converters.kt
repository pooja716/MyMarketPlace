package com.example.mymarketplace.data.local.converter

import androidx.room.TypeConverter
import com.example.mymarketplace.domain.model.SyncStatus
import com.example.mymarketplace.domain.model.PendingAction

class Converters {
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)

    @TypeConverter
    fun fromPendingAction(value: PendingAction?): String? = value?.name

    @TypeConverter
    fun toPendingAction(value: String?): PendingAction? = value?.let { PendingAction.valueOf(it) }
}
