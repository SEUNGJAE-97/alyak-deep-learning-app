package com.alyak.detector.feature.pill.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.repository.PillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
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

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val pills: List<Pill>) : SearchUiState
    data class Error(val message: String) : SearchUiState
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

    private val _searchUiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    fun searchPills(shape: String, color: String, line: String) {
        viewModelScope.launch {
            _searchUiState.value = SearchUiState.Loading
            try {
                delay(5000L)
                val result = repository.searchPills(shape, color, line)
                _searchUiState.value = SearchUiState.Success(result)
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}