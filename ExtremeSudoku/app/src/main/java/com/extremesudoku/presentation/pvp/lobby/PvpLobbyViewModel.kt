package com.extremesudoku.presentation.pvp.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.models.pvp.PvpMode
import com.extremesudoku.domain.repository.PvpMatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
     * Aktif olarak diğer oyuncuları arar (her 2 saniyede bir)
     */
    private fun startActiveMatchmaking(mode: PvpMode) {
        android.util.Log.d("PvpLobby", "🚀 Aktif matchmaking başlatıldı")
        
        viewModelScope.launch {
            var attemptCount = 0
            
            while (_uiState.value is PvpLobbyState.Searching) {
                attemptCount++
                android.util.Log.d("PvpLobby", "🔍 Matchmaking denemesi #$attemptCount")
                
                // Hala aranıyor mu kontrol et
                if (_uiState.value !is PvpLobbyState.Searching) {
                    android.util.Log.d("PvpLobby", "⏹️ Matchmaking durduruldu")
                    break
                }
                
                // Matchmaking dene
                repository.tryMatchmaking(mode).fold(
                    onSuccess = { matchId ->
                        if (matchId != null) {
                            // Eşleşme bulundu!
                            android.util.Log.d("PvpLobby", "🎉 EŞLEŞME BULUNDU! MatchID: $matchId")
                            _uiState.value = PvpLobbyState.MatchFound(matchId)
                        } else {
                            android.util.Log.d("PvpLobby", "⏳ Henüz rakip yok, denemeye devam...")
                        }
                    },
                    onFailure = { error ->
                        android.util.Log.e("PvpLobby", "❌ Matchmaking deneme hatası: ${error.message}", error)
                        // Hata olsa bile devam et
                    }
                )
                
                // 2 saniye bekle (daha hızlı)
                delay(2000)
            }
            
            android.util.Log.d("PvpLobby", "🛑 Aktif matchmaking sonlandı (toplam $attemptCount deneme)")
        }
    }
    
    /**
     * Matchmaking durumunu dinle (PASSIVE MATCHMAKING)
     * Başka bir oyuncu bizim için match oluşturabilir!
     */
    private suspend fun observeMatchmaking() {
        repository.observeMatchmaking().collectLatest { request ->
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
