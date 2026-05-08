package com.example.mymarketplace.presentation.listing.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mymarketplace.R

enum class ListingTab { ALL, FAVORITES, PENDING }

@Composable
fun ListingTabs(selected: ListingTab, onTabSelected: (ListingTab) -> Unit) {
    val tabs = listOf(
        stringResource(R.string.tab_all),
        stringResource(R.string.tab_favorites),
        stringResource(R.string.tab_pending_sync)
    )
    TabRow(selectedTabIndex = selected.ordinal) {
        tabs.forEachIndexed { i, title ->
            Tab(
                selected = selected.ordinal == i,
                onClick = { onTabSelected(ListingTab.entries[i]) },
                text = { Text(title, style = MaterialTheme.typography.labelMedium) }
            )
        }
    }
}
