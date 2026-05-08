package com.example.mymarketplace.presentation.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymarketplace.domain.model.CreateListingInput
import com.example.mymarketplace.domain.repository.ListingRepository
import com.example.mymarketplace.domain.usecase.CreateListingUseCase
import com.example.mymarketplace.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val createListingUseCase: CreateListingUseCase,
    private val repository: ListingRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    private val _descriptionError = MutableStateFlow<String?>(null)
    val descriptionError: StateFlow<String?> = _descriptionError.asStateFlow()

    private val _priceError = MutableStateFlow<String?>(null)
    val priceError: StateFlow<String?> = _priceError.asStateFlow()

    private val _events = Channel<CreateListingEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onTitleChange(value: String) {
        _title.value = value
        if (value.isNotBlank()) _titleError.value = null
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
        if (value.isNotBlank()) _descriptionError.value = null
    }

    fun onPriceChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _price.value = value
            if (value.isNotBlank()) _priceError.value = null
        }
    }

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun onImageCaptured(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun clearImage() {
        _selectedImageUri.value = null
    }

    fun onSubmit() {
        if (!validate()) return

        viewModelScope.launch {
            _isLoading.value = true
            val input = CreateListingInput(
                title = _title.value.trim(),
                description = _description.value.trim(),
                price = _price.value.toDouble(),
                localImagePath = _selectedImageUri.value?.toString()
            )
            createListingUseCase(input)
                .onSuccess {
                    if (connectivityObserver.isOnline()) {
                        repository.syncPendingListings()
                    }
                    _events.send(CreateListingEvent.Success)
                }
                .onFailure { error ->
                    _events.send(CreateListingEvent.Error(error.message ?: "Something went wrong"))
                }
            _isLoading.value = false
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (_title.value.isBlank()) {
            _titleError.value = "Title is required"
            isValid = false
        } else if (_title.value.trim().length < 3) {
            _titleError.value = "Title must be at least 3 characters"
            isValid = false
        }

        if (_description.value.isBlank()) {
            _descriptionError.value = "Description is required"
            isValid = false
        }

        if (_price.value.isBlank()) {
            _priceError.value = "Price is required"
            isValid = false
        } else if (_price.value.toDoubleOrNull() == null) {
            _priceError.value = "Enter a valid price"
            isValid = false
        } else if (_price.value.toDouble() <= 0) {
            _priceError.value = "Price must be greater than 0"
            isValid = false
        }

        return isValid
    }
}

sealed interface CreateListingEvent {
    object Success : CreateListingEvent
    data class Error(val message: String) : CreateListingEvent
}
