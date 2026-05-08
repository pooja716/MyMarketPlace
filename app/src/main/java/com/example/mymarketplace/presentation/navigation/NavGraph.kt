package com.example.mymarketplace.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mymarketplace.presentation.create.CreateListingScreen
import com.example.mymarketplace.presentation.detail.ListingDetailScreen
import com.example.mymarketplace.presentation.listing.ListingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "listings") {
        composable("listings") {
            ListingsScreen(
                onNavigateToCreate = { navController.navigate("create") },
                onNavigateToDetail = { id -> navController.navigate("detail/$id") }
            )
        }
        composable("create") {
            CreateListingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "detail/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: return@composable
            ListingDetailScreen(
                listingId = listingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
