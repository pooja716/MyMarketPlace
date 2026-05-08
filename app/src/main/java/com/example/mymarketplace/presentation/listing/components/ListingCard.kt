package com.example.mymarketplace.presentation.listing.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mymarketplace.R
import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.model.SyncStatus
import com.example.mymarketplace.ui.theme.Dimens
import com.example.mymarketplace.ui.theme.FavoriteIconTint
import com.example.mymarketplace.ui.theme.PendingBadgeBackground
import com.example.mymarketplace.ui.theme.PendingBadgeText
import com.example.mymarketplace.ui.theme.PriceText

@Composable
fun ListingCard(
    listing: Listing,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(0.75f),
        shape = RoundedCornerShape(Dimens.size12),
        border = BorderStroke(Dimens.size1, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(listing.localImagePath ?: listing.imageUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .size(320, 240)
                    .build(),
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(Dimens.size130)
            )

            if (listing.syncStatus != SyncStatus.SYNCED) {
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(Dimens.size6),
                    shape = RoundedCornerShape(Dimens.size4),
                    color = PendingBadgeBackground
                ) {
                    Text(
                        stringResource(R.string.badge_pending),
                        modifier = Modifier.padding(horizontal = Dimens.size6, vertical = Dimens.size2),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 0.4.sp,
                            color = PendingBadgeText
                        )
                    )
                }
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.size28)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (listing.isFavorite) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (listing.isFavorite) stringResource(R.string.cd_unfavorite)
                        else stringResource(R.string.cd_favorite),
                        tint = if (listing.isFavorite) FavoriteIconTint
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.size16)
                    )
                }
            }
        }

        Column(Modifier.padding(start = Dimens.size10, end = Dimens.size10, top = Dimens.size8, bottom = Dimens.size10)) {
            Text(
                listing.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                listing.description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Dimens.size6))
            Text(
                "$${listing.price}",
                style = MaterialTheme.typography.titleSmall,
                color = PriceText
            )
        }
    }
}
