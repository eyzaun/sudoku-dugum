package com.extremesudoku.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.preferences.ThemeManager
import com.extremesudoku.domain.repositories.UserPreferencesRepository
import com.extremesudoku.presentation.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val highlightConflicts: Boolean = true,
    val highlightSameNumbers: Boolean = true,
    val showRemainingNumbers: Boolean = true,
    val showTimer: Boolean = true,
    val autoCheckMistakes: Boolean = false,
    val highlightSelectedArea: Boolean = true,  // Row/Column/Box highlight
    val autoRemoveNotes: Boolean = true,  // Auto-remove notes
    val showAffectedAreas: Boolean = true,  // Show affected areas from number pad
    val showScoreAndStreak: Boolean = true,  // NEW: Show score and streak
    val colorizeNumbers: Boolean = true,  // NEW: Colorize numbers
    val currentTheme: ThemeType = ThemeType.LIGHT
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val themeManager: ThemeManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                preferencesRepository.soundEnabled,
                preferencesRepository.vibrationEnabled,
                preferencesRepository.highlightConflicts,
                preferencesRepository.highlightSameNumbers,
                preferencesRepository.showRemainingNumbers,
                preferencesRepository.showTimer,
                preferencesRepository.autoCheckMistakes,
                preferencesRepository.highlightSelectedArea,
                preferencesRepository.autoRemoveNotes,
                preferencesRepository.showAffectedAreas,
                preferencesRepository.showScoreAndStreak,  // NEW
                preferencesRepository.colorizeNumbers,  // NEW
                themeManager.themeType
            ) { flows ->
                val sound = flows[0] as Boolean
                val vibration = flows[1] as Boolean
                val conflicts = flows[2] as Boolean
                val sameNumbers = flows[3] as Boolean
                val remainingNumbers = flows[4] as Boolean
                val timer = flows[5] as Boolean
                val autoCheck = flows[6] as Boolean
                val selectedArea = flows[7] as Boolean
                val removeNotes = flows[8] as Boolean
                val affectedAreas = flows[9] as Boolean
                val scoreStreak = flows[10] as Boolean  // NEW
                val colorize = flows[11] as Boolean  // NEW
                val theme = flows[12] as ThemeType
                SettingsUiState(
                    soundEnabled = sound,
                    vibrationEnabled = vibration,
                    highlightConflicts = conflicts,
                    highlightSameNumbers = sameNumbers,
                    showRemainingNumbers = remainingNumbers,
                    showTimer = timer,
                    autoCheckMistakes = autoCheck,
                    highlightSelectedArea = selectedArea,
                    autoRemoveNotes = removeNotes,
                    showAffectedAreas = affectedAreas,
                    showScoreAndStreak = scoreStreak,  // NEW
                    colorizeNumbers = colorize,  // NEW
                    currentTheme = theme
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun toggleSound() {
        viewModelScope.launch {
            preferencesRepository.setSoundEnabled(!_uiState.value.soundEnabled)
        }
    }
    
    fun toggleVibration() {
        viewModelScope.launch {
            preferencesRepository.setVibrationEnabled(!_uiState.value.vibrationEnabled)
        }
    }
    
    fun toggleHighlightConflicts() {
        viewModelScope.launch {
            preferencesRepository.setHighlightConflicts(!_uiState.value.highlightConflicts)
        }
    }
    
    fun toggleAutoCheckMistakes() {
        viewModelScope.launch {
            preferencesRepository.setAutoCheckMistakes(!_uiState.value.autoCheckMistakes)
        }
    }
    
    fun toggleHighlightSameNumbers() {
        viewModelScope.launch {
            preferencesRepository.setHighlightSameNumbers(!_uiState.value.highlightSameNumbers)
        }
    }
    
    fun toggleShowRemainingNumbers() {
        viewModelScope.launch {
            preferencesRepository.setShowRemainingNumbers(!_uiState.value.showRemainingNumbers)
        }
    }
    
    fun toggleShowTimer() {
        viewModelScope.launch {
            preferencesRepository.setShowTimer(!_uiState.value.showTimer)
        }
    }
    
    fun toggleHighlightSelectedArea() {
        viewModelScope.launch {
            preferencesRepository.setHighlightSelectedArea(!_uiState.value.highlightSelectedArea)
        }
    }
    
    fun toggleAutoRemoveNotes() {
        viewModelScope.launch {
            preferencesRepository.setAutoRemoveNotes(!_uiState.value.autoRemoveNotes)
        }
    }
    
    fun toggleShowAffectedAreas() {
        viewModelScope.launch {
            preferencesRepository.setShowAffectedAreas(!_uiState.value.showAffectedAreas)
        }
    }
    
    fun toggleShowScoreAndStreak() {
        viewModelScope.launch {
            preferencesRepository.setShowScoreAndStreak(!_uiState.value.showScoreAndStreak)
        }
    }
    
    fun toggleColorizeNumbers() {
        viewModelScope.launch {
            preferencesRepository.setColorizeNumbers(!_uiState.value.colorizeNumbers)
        }
    }
    
    fun setTheme(themeType: ThemeType) {
        viewModelScope.launch {
            themeManager.setThemeType(themeType)
        }
    }
}
