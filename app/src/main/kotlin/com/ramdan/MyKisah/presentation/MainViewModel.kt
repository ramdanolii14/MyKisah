package com.ramdan.MyKisah.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramdan.MyKisah.data.repository.PhotoLocationRepository
import com.ramdan.MyKisah.domain.model.PhotoLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val photos: List<PhotoLocation> = emptyList(),
    val isSyncing: Boolean = false,
    val error: String? = null,
    val selectedIndex: Int? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PhotoLocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.photoLocations.collect { photos ->
                _uiState.update { it.copy(photos = photos) }
            }
        }
    }

    fun syncPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }
            try {
                repository.syncFromCamera()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }
    }

    fun selectPhoto(index: Int) {
        _uiState.update { it.copy(selectedIndex = index) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
