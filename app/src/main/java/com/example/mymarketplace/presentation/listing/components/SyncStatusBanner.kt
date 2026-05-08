package com.example.mymarketplace.presentation.listing.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.mymarketplace.R
import com.example.mymarketplace.presentation.common.UiSyncStatus
import com.example.mymarketplace.ui.theme.Dimens
import com.example.mymarketplace.ui.theme.OfflineBannerBackground
import com.example.mymarketplace.ui.theme.OfflineBannerTint
import com.example.mymarketplace.ui.theme.SyncingBannerBackground
import com.example.mymarketplace.ui.theme.SyncingBannerTint

@Composable
fun SyncStatusBanner(isOnline: Boolean, syncStatus: UiSyncStatus) {
    AnimatedVisibility(
        visible = !isOnline || syncStatus == UiSyncStatus.SYNCING,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val (bg, tint, icon, text) = when {
            !isOnline -> BannerConfig(
                OfflineBannerBackground, OfflineBannerTint,
                Icons.Default.WifiOff, stringResource(R.string.banner_offline)
            )
            else -> BannerConfig(
                SyncingBannerBackground, SyncingBannerTint,
                Icons.Default.Sync, stringResource(R.string.banner_syncing)
            )
        }
        Surface(color = bg) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = Dimens.size16, vertical = Dimens.size8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.size8)
            ) {
                if (syncStatus == UiSyncStatus.SYNCING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.size14),
                        strokeWidth = Dimens.size2,
                        color = tint
                    )
                } else {
                    Icon(icon, null, tint = tint, modifier = Modifier.size(Dimens.size15))
                }
                Text(text, style = MaterialTheme.typography.labelMedium, color = tint)
            }
        }
    }
}

private data class BannerConfig(
    val bg: Color,
    val tint: Color,
    val icon: ImageVector,
    val text: String
)
