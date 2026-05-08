package com.example.mymarketplace.data.scheduler

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mymarketplace.data.service.SyncWorker
import com.example.mymarketplace.domain.scheduler.SyncScheduler
import javax.inject.Inject

class SyncSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : SyncScheduler {

    override fun scheduleSync() {
        workManager.enqueue(
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }
}
