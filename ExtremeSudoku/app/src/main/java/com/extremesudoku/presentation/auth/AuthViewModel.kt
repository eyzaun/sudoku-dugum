package com.extremesudoku.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }
    
    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }
    
    fun onSignInClicked() {
        if (!validateInputs()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = userRepository.signIn(
                _uiState.value.email,
                _uiState.value.password
            )
            
            result.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false, navigateToHome = true)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "Sign in failed")
                }
            }
        }
    }
    
    fun onSignUpClicked() {
        if (!validateInputs()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = userRepository.signUp(
                _uiState.value.email,
                _uiState.value.password
            )
            
            result.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false, navigateToHome = true)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "Sign up failed")
                }
            }
        }
    }
    
    fun onContinueAsGuestClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = userRepository.continueAsGuest()
            
            result.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false, navigateToHome = true)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "Failed to continue as guest")
                }
            }
        }
    }
    
    fun onNavigationComplete() {
        _uiState.update { it.copy(navigateToHome = false) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    private fun validateInputs(): Boolean {
        val email = _uiState.value.email
        val password = _uiState.value.password
        
        when {
            email.isBlank() -> {
                _uiState.update { it.copy(error = "Email is required") }
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(error = "Invalid email format") }
                return false
            }
            password.length < 6 -> {
                _uiState.update { it.copy(error = "Password must be at least 6 characters") }
                return false
            }
        }
        return true
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val navigateToHome: Boolean = false,
    val error: String? = null
)
