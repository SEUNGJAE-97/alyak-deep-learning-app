package com.alyak.detector.feature.pill.ui.PillDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.feature.pill.data.model.MedicineDetailDto
import com.alyak.detector.feature.pill.data.repository.PillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PillDetailUiState {
    data object Loading : PillDetailUiState()
    data class Success(val detail: MedicineDetailDto) : PillDetailUiState()
    data object Error : PillDetailUiState()
}

@HiltViewModel
class PillDetailViewModel @Inject constructor(
    private val repository: PillRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val pillId: Long = savedStateHandle.get<Long>("pillId") ?: 0L
    private val _uiState = MutableStateFlow<PillDetailUiState>(PillDetailUiState.Loading)
    val uiState: StateFlow<PillDetailUiState> = _uiState

    init {
        loadPillDetail(pillId)
    }

    private fun loadPillDetail(pid: Long) {
        viewModelScope.launch {
            _uiState.value = PillDetailUiState.Loading
            try {
                val detailDto = repository.searchPillDetail(pid)
                _uiState.value = PillDetailUiState.Success(detailDto)
            } catch (e: Exception) {
                _uiState.value = PillDetailUiState.Error
            }
        }
    }
}