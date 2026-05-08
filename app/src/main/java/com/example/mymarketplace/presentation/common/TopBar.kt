package com.example.mymarketplace.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mymarketplace.R
import com.example.mymarketplace.ui.theme.Dimens

@Composable
fun TopBar(isOnline: Boolean) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(stringResource(R.string.topbar_title), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.topbar_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.size6)) {
            Box(
                Modifier.size(Dimens.size8).background(
                    color = if (isOnline) Color(0xFF1D9E75) else Color(0xFFE24B4A),
                    shape = CircleShape
                )
            )
            Text(
                if (isOnline) stringResource(R.string.status_online) else stringResource(R.string.status_offline),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider()
}
