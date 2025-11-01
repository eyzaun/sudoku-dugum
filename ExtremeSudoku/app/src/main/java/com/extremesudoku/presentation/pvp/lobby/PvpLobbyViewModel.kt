package com.extremesudoku.presentation.pvp.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.models.pvp.PvpMode
import com.extremesudoku.domain.repository.PvpMatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * PvP Lobby (Matchmaking) ViewModel
 */
@HiltViewModel
class PvpLobbyViewModel @Inject constructor(
    private val repository: PvpMatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PvpLobbyState>(PvpLobbyState.Idle)
    val uiState: StateFlow<PvpLobbyState> = _uiState.asStateFlow()

    private var selectedMode: PvpMode = PvpMode.BLIND_RACE

    // ⚡ FIX: Active matchmaking'i kontrol etmek için ayrı flag
    // UI state değişip de isChecking = false olabilir ama aktif polling devam etmeli
    private var isActiveMatchmakingRunning = false
    
    /**
     * Matchmaking'e katıl ve eşleşme dinlemeye başla
     */
    fun startMatchmaking(mode: PvpMode) {
        selectedMode = mode
        android.util.Log.d("PvpLobby", "🎮 Matchmaking başlatıldı - Mod: $mode")
        
        viewModelScope.launch {
            _uiState.value = PvpLobbyState.Searching(mode)
            
            // Matchmaking kuyruğuna katıl
            repository.joinMatchmaking(mode).fold(
                onSuccess = {
                    android.util.Log.d("PvpLobby", "✅ Kuyruğa katılım başarılı")
                    
                    // İKİ AYRI COROUTINE BAŞLAT - birbirini bloklamasın!
                    // 1. Firestore'u sürekli dinle (passive)
                    viewModelScope.launch {
                        observeMatchmaking()
                    }
                    
                    // 2. Aktif olarak eşleşme ara (active)
                    viewModelScope.launch {
                        startActiveMatchmaking(mode)
                    }
                },
                onFailure = { error ->
                    android.util.Log.e("PvpLobby", "❌ Kuyruğa katılım hatası: ${error.message}", error)
                    _uiState.value = PvpLobbyState.Error(
                        error.message ?: "Matchmaking'e katılırken hata oluştu"
                    )
                }
            )
        }
    }
    
    /**
     * Aktif olarak diğer oyuncuları arar (her 3 saniyede bir)
     * OPTIMIZASYON: Polling aralığını artırıldı, server yükü azaltıldı
     */
    private fun startActiveMatchmaking(mode: PvpMode) {
        android.util.Log.d("PvpLobby", "🚀 Aktif matchmaking başlatıldı")

        viewModelScope.launch {
            var attemptCount = 0
            var timeoutSeconds = 0
            val maxTimeoutSeconds = 180  // 3 dakika sonra timeout

            // ⚡ FIX: Independent flag başlat - passive listener state değişikliklerinden bağımsız
            isActiveMatchmakingRunning = true
            android.util.Log.d("PvpLobby", "✅ Aktif matchmaking flag set: true")

            while (isActiveMatchmakingRunning && timeoutSeconds < maxTimeoutSeconds) {
                attemptCount++
                android.util.Log.d("PvpLobby", "🔍 Matchmaking denemesi #$attemptCount (${timeoutSeconds}s / ${maxTimeoutSeconds}s)")

                // Matchmaking dene
                repository.tryMatchmaking(mode).fold(
                    onSuccess = { matchId ->
                        if (matchId != null) {
                            // Eşleşme bulundu!
                            android.util.Log.d("PvpLobby", "🎉 EŞLEŞME BULUNDU! MatchID: $matchId")
                            _uiState.value = PvpLobbyState.MatchFound(matchId)
                            isActiveMatchmakingRunning = false  // ⚡ FIX: Loop'u durdur
                        } else {
                            android.util.Log.d("PvpLobby", "⏳ Henüz rakip yok (attempt #$attemptCount), denemeye devam...")
                        }
                    },
                    onFailure = { error ->
                        // ⚡ DIAGNOSIS: Log the full error stack for debugging
                        android.util.Log.e("PvpLobby", "❌ Matchmaking deneme hatası #$attemptCount: ${error.message}", error)

                        // Check for specific error types
                        if (error.message?.contains("index", ignoreCase = true) == true) {
                            android.util.Log.w("PvpLobby", "⚠️ CRITICAL: Firestore composite index eksik!")
                            android.util.Log.w("PvpLobby", "📱 Firebase Console'da şu index'i oluştur:")
                            android.util.Log.w("PvpLobby", "   Collection: matchmaking_queue")
                            android.util.Log.w("PvpLobby", "   Fields: status, mode, timestamp (all Ascending)")
                        }

                        // Continue polling even on errors
                    }
                )

                // Match bulunmadıysa, 3 saniye bekle (server yükü azaltımı)
                if (isActiveMatchmakingRunning) {
                    delay(3000)
                    timeoutSeconds += 3
                }
            }

            // Timeout kontrolü
            if (isActiveMatchmakingRunning && timeoutSeconds >= maxTimeoutSeconds) {
                android.util.Log.w("PvpLobby", "⏱️ TIMEOUT! 3 dakika sonra matchmaking iptal edildi")
                _uiState.value = PvpLobbyState.Error("Eşleşme bulunamadı (zaman aşımı)")
                isActiveMatchmakingRunning = false  // ⚡ FIX: Flag'i sıfırla
            }

            android.util.Log.d("PvpLobby", "🛑 Aktif matchmaking sonlandı (toplam $attemptCount deneme, $timeoutSeconds saniye)")
        }
    }
    
    /**
     * Matchmaking durumunu dinle (PASSIVE MATCHMAKING)
     * Başka bir oyuncu bizim için match oluşturabilir!
     */
    private suspend fun observeMatchmaking() {
        // ✅ FIX: Lifecycle-aware flow collection
        repository.observeMatchmaking()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
            .collectLatest { request ->
                when {
                    request == null -> {
                        // Kullanıcı kuyruktan çıktı
                        android.util.Log.d("PvpLobby", "📭 Matchmaking kaydı yok")
                        _uiState.value = PvpLobbyState.Idle
                    }
                    request.status == "matched" && request.matchId != null -> {
                        // ⚡ PASSIVE MATCH: Başka biri bizim için match oluşturdu!
                        android.util.Log.d("PvpLobby", "🎉 PASSIVE MATCH BULUNDU! MatchID: ${request.matchId}")
                        _uiState.value = PvpLobbyState.MatchFound(request.matchId)
                    }
                    request.status == "cancelled" -> {
                        // İptal edildi
                        android.util.Log.d("PvpLobby", "❌ Matchmaking iptal edildi")
                        _uiState.value = PvpLobbyState.Cancelled
                    }
                    request.status == "searching" -> {
                        // Hala aranıyor
                        android.util.Log.d("PvpLobby", "🔍 Hala aranıyor...")
                        _uiState.value = PvpLobbyState.Searching(selectedMode)
                    }
                }
            }
    }
    
    /**
     * Matchmaking'i iptal et
     */
    fun cancelMatchmaking() {
        isActiveMatchmakingRunning = false  // ⚡ FIX: Flag'i sıfırla - loop'u durdur
        viewModelScope.launch {
            repository.leaveMatchmaking().fold(
                onSuccess = {
                    _uiState.value = PvpLobbyState.Cancelled
                },
                onFailure = { error ->
                    _uiState.value = PvpLobbyState.Error(
                        error.message ?: "İptal edilirken hata oluştu"
                    )
                }
            )
        }
    }
    
    /**
     * Error'ı temizle
     */
    fun clearError() {
        if (_uiState.value is PvpLobbyState.Error) {
            _uiState.value = PvpLobbyState.Idle
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // ViewModel destroy olurken matchmaking'den çık
        viewModelScope.launch {
            repository.leaveMatchmaking()
        }
    }
}

/**
 * PvP Lobby UI State
 */
sealed class PvpLobbyState {
    object Idle : PvpLobbyState()
    data class Searching(val mode: PvpMode) : PvpLobbyState()
    data class MatchFound(val matchId: String) : PvpLobbyState()
    object Cancelled : PvpLobbyState()
    data class Error(val message: String) : PvpLobbyState()
}
