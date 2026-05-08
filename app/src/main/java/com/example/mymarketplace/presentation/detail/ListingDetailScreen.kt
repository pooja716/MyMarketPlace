package com.example.mymarketplace.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mymarketplace.R
import com.example.mymarketplace.domain.model.SyncStatus
import com.example.mymarketplace.ui.theme.Dimens
import com.example.mymarketplace.ui.theme.FavoriteIconTint
import com.example.mymarketplace.ui.theme.PendingBadgeBackground
import com.example.mymarketplace.ui.theme.PendingBadgeText
import com.example.mymarketplace.ui.theme.PriceText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: String,
    onNavigateBack: () -> Unit,
    viewModel: ListingDetailViewModel = hiltViewModel()
) {
    val listing by viewModel.listing.collectAsStateWithLifecycle()

    LaunchedEffect(listingId) {
        viewModel.loadListing(listingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    listing?.let { l ->
                        IconButton(onClick = { viewModel.onFavoriteToggle(l.id) }) {
                            Icon(
                                imageVector = if (l.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (l.isFavorite) stringResource(R.string.cd_unfavorite) else stringResource(R.string.cd_favorite),
                                tint = if (l.isFavorite) FavoriteIconTint else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val item = listing
        if (item == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.localImagePath ?: item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(Dimens.size260)
                )

                Column(Modifier.padding(Dimens.size16), verticalArrangement = Arrangement.spacedBy(Dimens.size8)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            item.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "$${item.price}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = PriceText
                        )
                    }

                    if (item.syncStatus != SyncStatus.SYNCED) {
                        Surface(
                            shape = RoundedCornerShape(Dimens.size4),
                            color = PendingBadgeBackground
                        ) {
                            Text(
                                stringResource(R.string.label_pending_sync),
                                modifier = Modifier.padding(horizontal = Dimens.size8, vertical = Dimens.size4),
                                style = MaterialTheme.typography.labelSmall,
                                color = PendingBadgeText
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        stringResource(R.string.label_description_header),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(item.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
