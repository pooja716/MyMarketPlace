package com.example.mymarketplace.presentation.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mymarketplace.data.sync.SyncWorker
import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.usecase.ObserveListingsUseCase
import com.example.mymarketplace.domain.usecase.RefreshListingsUseCase
import com.example.mymarketplace.domain.usecase.SyncPendingListingsUseCase
import com.example.mymarketplace.domain.usecase.ToggleFavoriteUseCase
import com.example.mymarketplace.presentation.common.UiSyncStatus
import com.example.mymarketplace.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingsViewModel @Inject constructor(
    private val observeListings: ObserveListingsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val refreshListings: RefreshListingsUseCase,
    private val syncPendingListings: SyncPendingListingsUseCase,
    private val connectivityObserver: ConnectivityObserver,
    private val workManager: WorkManager
) : ViewModel() {

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    private val _isOnline = MutableStateFlow(connectivityObserver.isOnline())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _syncStatus = MutableStateFlow(UiSyncStatus.IDLE)
    val syncStatus: StateFlow<UiSyncStatus> = _syncStatus.asStateFlow()

    init {
        viewModelScope.launch {
            observeListings().collect { _listings.value = it }
        }

        viewModelScope.launch {
            refreshListings()
            if (connectivityObserver.isOnline()) {
                runSync()
            }
        }

        connectivityObserver.observe()
            .onEach { status ->
                val online = status == ConnectivityObserver.Status.Available
                _isOnline.value = online
                if (online) triggerSync()
            }
            .launchIn(viewModelScope)
    }

    fun onFavoriteToggle(id: String) {
        viewModelScope.launch {
            toggleFavorite(id)
        }
    }

    fun triggerSync() {
        viewModelScope.launch { runSync() }

        workManager.enqueue(
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .build()
        )
    }

    private suspend fun runSync() {
        _syncStatus.value = UiSyncStatus.SYNCING
        syncPendingListings()
        _syncStatus.value = UiSyncStatus.IDLE
    }
}
