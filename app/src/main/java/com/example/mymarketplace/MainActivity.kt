package com.example.mymarketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mymarketplace.presentation.navigation.NavGraph
import com.example.mymarketplace.ui.theme.MyMarketPlaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMarketPlaceTheme {
                NavGraph()
            }
        }
    }
}
