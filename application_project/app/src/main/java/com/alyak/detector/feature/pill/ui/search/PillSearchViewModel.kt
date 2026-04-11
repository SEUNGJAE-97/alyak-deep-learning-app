package com.alyak.detector.feature.pill.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.auth.SessionManager
import com.alyak.detector.core.auth.UserSession
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import com.alyak.detector.feature.pill.data.repository.PillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
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
    private val repository: PillRepository,
    private val recentSearchDao: RecentSearchDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    // 자동완성
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .filter { it.length >= 2 }
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchAutocomplete(query)
                }
        }
    }

    val userName: StateFlow<String> = sessionManager.userSession
        .map { session ->
            when (session) {
                is UserSession.Authenticated -> session.userInfo.name
                else -> "로딩 중.."
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "로딩 중.."
        )

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

    private val _searchUiState: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.Idle)
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

    fun findPillsByName(name: String) {
        viewModelScope.launch {
            _searchUiState.value = SearchUiState.Loading
            try {
                val result = repository.findPills(name)
                _searchUiState.value = SearchUiState.Success(result)
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            recentSearchDao.deleteAll()
        }
    }

    fun onSearch(query: String) {
        _suggestions.value = emptyList()
        findPillsByName(query)
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _suggestions.value = emptyList()
        }
    }

    /**
     * 사용자가 선택한 텍스트를 검색창에 넣어줌
     * */
    fun onSuggestionSelected(suggestion: String) {
        _searchQuery.value = suggestion
        _suggestions.value = emptyList()
    }

    private suspend fun fetchAutocomplete(query: String) {
        try {
            val result = repository.getAutocomplete(query)
            _suggestions.value = result
        } catch (e: Exception) {
            _suggestions.value = emptyList()
        }
    }
}