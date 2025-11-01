package com.extremesudoku.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _onboardingComplete = MutableStateFlow(false)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete.asStateFlow()

    fun nextPage() {
        if (_currentPage.value < TOTAL_PAGES - 1) {
            _currentPage.value++
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun goToPage(page: Int) {
        if (page in 0 until TOTAL_PAGES) {
            _currentPage.value = page
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            // Onboarding tamamlandı olarak işaretle (Preferences'a kaydet)
            userRepository.setOnboardingCompleted(true)
            _onboardingComplete.value = true
        }
    }

    companion object {
        const val TOTAL_PAGES = 5
    }
}
