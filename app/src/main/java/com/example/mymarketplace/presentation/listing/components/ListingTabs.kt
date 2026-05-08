package com.example.mymarketplace.presentation.listing.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class ListingTab { ALL, FAVORITES, PENDING }

@Composable
fun ListingTabs(selected: ListingTab, onTabSelected: (ListingTab) -> Unit) {
    TabRow(selectedTabIndex = selected.ordinal) {
        listOf("All", "Favorites", "Pending sync").forEachIndexed { i, title ->
            Tab(
                selected = selected.ordinal == i,
                onClick = { onTabSelected(ListingTab.entries[i]) },
                text = { Text(title, style = MaterialTheme.typography.labelMedium) }
            )
        }
    }
}
