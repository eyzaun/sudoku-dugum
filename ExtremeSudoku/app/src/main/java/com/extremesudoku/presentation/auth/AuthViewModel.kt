package com.extremesudoku.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.R
import com.extremesudoku.data.repository.UserRepository
import com.extremesudoku.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val resourceProvider: ResourceProvider
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
                val message = error.message ?: resourceProvider.getString(R.string.error_sign_in_failed)
                _uiState.update {
                    it.copy(isLoading = false, error = message)
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
                val message = error.message ?: resourceProvider.getString(R.string.error_sign_up_failed)
                _uiState.update {
                    it.copy(isLoading = false, error = message)
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
                val message = error.message ?: resourceProvider.getString(R.string.error_guest_mode_failed)
                _uiState.update {
                    it.copy(isLoading = false, error = message)
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
                _uiState.update { it.copy(error = resourceProvider.getString(R.string.error_email_required)) }
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(error = resourceProvider.getString(R.string.error_invalid_email_format)) }
                return false
            }
            password.length < 6 -> {
                _uiState.update { it.copy(error = resourceProvider.getString(R.string.error_password_too_short)) }
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
