package com.example.mymarketplace.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymarketplace.domain.model.Listing
import com.example.mymarketplace.domain.usecase.GetListingByIdUseCase
import com.example.mymarketplace.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    private val getListingById: GetListingByIdUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _listing = MutableStateFlow<Listing?>(null)
    val listing: StateFlow<Listing?> = _listing.asStateFlow()

    fun loadListing(id: String) {
        viewModelScope.launch {
            _listing.value = getListingById(id)
        }
    }

    fun onFavoriteToggle(id: String) {
        viewModelScope.launch {
            toggleFavorite(id)
            _listing.value = getListingById(id)
        }
    }
}
