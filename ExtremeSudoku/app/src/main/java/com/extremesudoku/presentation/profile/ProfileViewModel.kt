package com.extremesudoku.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.models.User
import com.extremesudoku.data.models.UserStats
import com.extremesudoku.data.repository.UserRepository
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadUserData()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            // Check if guest mode
            val isGuest = userRepository.isGuestMode()
            
            // Load user profile
            val user = userRepository.getCurrentUser()
            _uiState.update { it.copy(user = user, isGuestMode = isGuest) }
            
            // ✅ FIX: Lifecycle-aware flow collection
            userRepository.getUserStats()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null
                )
                .collect { stats ->
                    _uiState.update { it.copy(userStats = stats) }
                }
        }
    }
    
    fun onSignOutClicked() {
        userRepository.signOut()
        _uiState.update { it.copy(navigateToAuth = true) }
    }
    
    fun onNavigationComplete() {
        _uiState.update { it.copy(navigateToAuth = false) }
    }
}

data class ProfileUiState(
    val user: User? = null,
    val userStats: UserStats? = null,
    val isGuestMode: Boolean = false,
    val navigateToAuth: Boolean = false
)
