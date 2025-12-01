package com.alyak.detector.feature.pill.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.repository.PillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


sealed interface RecentSearchUiState {
    data object Loading : RecentSearchUiState
    data class Success(val pills: List<Pill>) : RecentSearchUiState
    data class Error(val message: String) : RecentSearchUiState
}

@HiltViewModel
class PillSearchViewModel @Inject constructor(
    private val repository: PillRepository
) : ViewModel() {
    val recentSearchState: StateFlow<RecentSearchUiState> = repository.fetchRecentPills()
        .map { pills ->
            RecentSearchUiState.Success(pills) as RecentSearchUiState
        }
        .onStart {
            emit(RecentSearchUiState.Loading)
        }
        .catch { e ->
            emit(RecentSearchUiState.Error(e.message ?: "Unknown Error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecentSearchUiState.Loading
        )

    private val _searchResult = MutableStateFlow<List<Pill>>(emptyList())
    val searchResultState = _searchResult.asStateFlow()

    fun searchPills(shape: String, color: String, line: String) {
        viewModelScope.launch {
            val result = repository.searchPills(shape, color, line)
            _searchResult.value = result
        }
    }
}