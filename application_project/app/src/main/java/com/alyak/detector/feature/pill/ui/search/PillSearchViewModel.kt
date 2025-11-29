package com.alyak.detector.feature.pill.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.repository.PillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


sealed interface RecentSearchUiState {
    data object Loading : RecentSearchUiState
    data class Success(val pills: List<Pill>) : RecentSearchUiState
    data class Error(val message: String) : RecentSearchUiState
}

@HiltViewModel
class PillSearchViewModel @Inject constructor(
    private val repository: PillRepository
): ViewModel() {
    private val _recentSearchState = MutableStateFlow<RecentSearchUiState>(RecentSearchUiState.Loading)
    val recentSearchState: StateFlow<RecentSearchUiState> = _recentSearchState.asStateFlow()

    init {
        getRecentSearches()
    }

    private fun getRecentSearches() {
        viewModelScope.launch {
            _recentSearchState.value = RecentSearchUiState.Loading

            repository.fetchRecentPills()
                .catch { e ->
                    _recentSearchState.value = RecentSearchUiState.Error(e.message ?: "Unknown Error")
                }
                .collect { pills ->
                    _recentSearchState.value = RecentSearchUiState.Success(pills)
                }
        }
    }
}