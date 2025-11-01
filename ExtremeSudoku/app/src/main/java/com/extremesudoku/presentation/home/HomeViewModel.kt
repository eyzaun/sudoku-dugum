package com.extremesudoku.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.models.GameState
import com.extremesudoku.data.models.UserStats
import com.extremesudoku.data.repository.SudokuRepository
import com.extremesudoku.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sudokuRepository: SudokuRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        // ✅ FIX: Lifecycle-aware flow collection with stateIn
        viewModelScope.launch {
            // Her iki flow'u da lifecycle-aware yap
            val activeGamesFlow = sudokuRepository.getActiveGames()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
            
            val userStatsFlow = userRepository.getUserStats()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null
                )
            
            // Combine ile iki flow'u birleştir
            combine(activeGamesFlow, userStatsFlow) { games, stats ->
                _uiState.update {
                    it.copy(
                        activeGames = games,
                        userStats = stats
                    )
                }
            }.collect { }
        }
        
        // Günlük challenge'ı kontrol et
        checkDailyChallenge()
    }
    
    fun onNewGameClicked(difficulty: String = "medium") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = sudokuRepository.getRandomSudoku(difficulty)
            result.onSuccess { sudoku ->
                // YENİ OYUN: sadece sudokuId gönder
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigateToNewGame = sudoku.id  // ← YENİ: sudoku.id
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }
    
    fun onContinueGameClicked(gameId: String) {
        // DEVAM EDEN OYUN: gameId gönder
        _uiState.update { it.copy(navigateToContinueGame = gameId) }  // ← YENİ
    }
    
    fun deleteGame(gameId: String) {
        viewModelScope.launch {
            // Oyunu "abandoned" olarak işaretle - silme yerine
            // Bu sayede bir daha bu puzzle denk gelmez
            sudokuRepository.abandonGame(gameId)
            // Aktif oyunlar otomatik güncellenir (Flow sayesinde - isAbandoned=0 filtrelenmiş)
        }
    }
    
    fun onDailyChallengeClicked() {
        viewModelScope.launch {
            // Günün sudokusunu getir
            val dailySudokuId = getDailySudokuId()
            // Daily challenge yeni oyun gibi davranır
            _uiState.update { it.copy(navigateToNewGame = dailySudokuId) }
        }
    }
    
    fun onNavigationComplete() {
        _uiState.update { 
            it.copy(
                navigateToNewGame = null,
                navigateToContinueGame = null
            ) 
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun toggleShowAllGames() {
        _uiState.update { it.copy(showAllGames = !it.showAllGames) }
    }
    
    private fun checkDailyChallenge() {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.YEAR) * 1000
        
        val lastPlayed = _uiState.value.userStats?.lastPlayedDate?.let { timestamp ->
            val lastCalendar = Calendar.getInstance()
            lastCalendar.timeInMillis = timestamp
            lastCalendar.get(Calendar.DAY_OF_YEAR) + lastCalendar.get(Calendar.YEAR) * 1000
        }
        
        _uiState.update {
            it.copy(isDailyChallengeAvailable = today != lastPlayed)
        }
    }
    
    private fun getDailySudokuId(): String {
        // Günün seed'ine göre belirli bir sudoku ID'si döndür
        val calendar = Calendar.getInstance()
        val seed = calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.YEAR) * 1000
        return "daily_$seed"
    }
}

data class HomeUiState(
    val activeGames: List<GameState> = emptyList(),
    val userStats: UserStats? = null,
    val isDailyChallengeAvailable: Boolean = true,
    val isLoading: Boolean = false,
    val navigateToNewGame: String? = null,        // ← YENİ: sudokuId
    val navigateToContinueGame: String? = null,   // ← YENİ: gameId
    val error: String? = null,
    val showAllGames: Boolean = false
)
