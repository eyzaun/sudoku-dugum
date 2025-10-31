package com.extremesudoku.presentation.pvp.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.data.models.pvp.PvpMatch
import com.extremesudoku.domain.repository.PvpMatchRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PvpResultViewModel @Inject constructor(
    private val repository: PvpMatchRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _resultState = MutableStateFlow<PvpResultState>(PvpResultState.Loading)
    val resultState: StateFlow<PvpResultState> = _resultState.asStateFlow()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun loadResult(matchId: String) {
        viewModelScope.launch {
            repository.getMatch(matchId)
                .onSuccess { match ->
                    val myData = match.players[currentUserId]
                    val opponentData = match.getOpponentData(currentUserId)
                    
                    if (myData == null || opponentData == null) {
                        _resultState.value = PvpResultState.Error("Oyuncu verileri bulunamadı")
                        return@onSuccess
                    }
                    
                    val myResult = myData.result
                    val opponentResult = opponentData.result
                    
                    if (myResult == null || opponentResult == null) {
                        _resultState.value = PvpResultState.Error("Sonuçlar henüz tamamlanmadı")
                        return@onSuccess
                    }
                    
                    val isCancelled = match.status == com.extremesudoku.data.models.pvp.MatchStatus.CANCELLED
                    
                    // CANCELLED durumunda winnerId'den bak (çünkü zaten doğru set edildi)
                    // Normal durumda da winnerId'den bak
                    val isWinner = match.winnerId == currentUserId
                    val isDraw = match.winnerId == null && !isCancelled  // Sadece normal bitişte berabere olabilir
                    
                    _resultState.value = PvpResultState.Success(
                        isWinner = isWinner,
                        isDraw = isDraw,
                        isCancelled = isCancelled,
                        myScore = myResult.score,
                        opponentScore = opponentResult.score,
                        myTime = myResult.timeElapsed,
                        opponentTime = opponentResult.timeElapsed,
                        myAccuracy = myResult.accuracy,
                        opponentAccuracy = opponentResult.accuracy,
                        myName = myData.displayName,
                        opponentName = opponentData.displayName,
                        mode = match.mode
                    )
                }
                .onFailure { error ->
                    _resultState.value = PvpResultState.Error(
                        error.message ?: "Sonuçlar yüklenirken hata oluştu"
                    )
                }
        }
    }
}

sealed class PvpResultState {
    object Loading : PvpResultState()
    data class Success(
        val isWinner: Boolean,
        val isDraw: Boolean,
        val isCancelled: Boolean,  // Birisi oyundan çıktı mı?
        val myScore: Int,
        val opponentScore: Int,
        val myTime: Long,
        val opponentTime: Long,
        val myAccuracy: Float,
        val opponentAccuracy: Float,
        val myName: String,
        val opponentName: String,
        val mode: com.extremesudoku.data.models.pvp.PvpMode
    ) : PvpResultState()
    data class Error(val message: String) : PvpResultState()
}
