package com.extremesudoku.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.models.LeaderboardEntry
import com.extremesudoku.data.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()
    
    init {
        loadLeaderboard()
    }
    
    private fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // ✅ FIX: Lifecycle-aware flow collection
            leaderboardRepository.getLeaderboard()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
                .collect { entries ->
                    _uiState.update { 
                        it.copy(
                            entries = entries,
                            isLoading = false
                        ) 
                    }
                }
        }
    }
    
    fun onRefresh() {
        loadLeaderboard()
    }
}

data class LeaderboardUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val isLoading: Boolean = true
)
