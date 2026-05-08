package com.example.mymarketplace.presentation.listing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymarketplace.R
import com.example.mymarketplace.domain.model.SyncStatus
import com.example.mymarketplace.presentation.common.TopBar
import com.example.mymarketplace.presentation.listing.components.ListingCard
import com.example.mymarketplace.presentation.listing.components.ListingTab
import com.example.mymarketplace.presentation.listing.components.ListingTabs
import com.example.mymarketplace.presentation.listing.components.SyncStatusBanner
import com.example.mymarketplace.ui.theme.Dimens

@Composable
fun ListingsScreen(
    viewModel: ListingsViewModel = hiltViewModel(),
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val listings by viewModel.listings.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(ListingTab.ALL) }

    Scaffold(
        topBar = { TopBar(isOnline = isOnline) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_create_listing))
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            SyncStatusBanner(isOnline = isOnline, syncStatus = syncStatus)

            ListingTabs(
                selected = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            val visibleListings = remember(listings, selectedTab) {
                listings.filter { listing ->
                    when (selectedTab) {
                        ListingTab.ALL -> true
                        ListingTab.FAVORITES -> listing.isFavorite
                        ListingTab.PENDING -> listing.syncStatus != SyncStatus.SYNCED
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = Dimens.size155),
                contentPadding = PaddingValues(Dimens.size12),
                verticalArrangement = Arrangement.spacedBy(Dimens.size12),
                horizontalArrangement = Arrangement.spacedBy(Dimens.size12),
                modifier = Modifier.weight(1f)
            ) {
                items(visibleListings, key = { it.id }) { listing ->
                    ListingCard(
                        listing = listing,
                        onFavoriteClick = { viewModel.onFavoriteToggle(listing.id) },
                        onClick = { onNavigateToDetail(listing.id) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.showing_listings, visibleListings.size, listings.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.size8),
                textAlign = TextAlign.Center
            )
        }
    }
}
