package com.extremesudoku.presentation.pvp.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.R
import com.extremesudoku.data.models.Cell
import com.extremesudoku.data.models.pvp.*
import com.extremesudoku.data.models.scoring.BonusEvent
import com.extremesudoku.data.models.scoring.BonusType
import com.extremesudoku.data.models.scoring.CompletionType
import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.domain.repository.PvpMatchRepository
import com.extremesudoku.domain.usecase.scoring.CalculateFinalScoreUseCase
import com.extremesudoku.domain.usecase.scoring.CalculateMoveScoreUseCase
import com.extremesudoku.domain.usecase.scoring.CheckCompletionBonusUseCase
import com.extremesudoku.utils.HapticFeedback
import com.extremesudoku.utils.SoundEffects
import com.extremesudoku.utils.NetworkMonitor
import com.extremesudoku.utils.NetworkStatus
import com.extremesudoku.utils.ErrorMessages
import com.extremesudoku.utils.ResourceProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

/**
 * Blind Race Mode ViewModel
 * - Oyuncular birbirlerinin hareketlerini GÖREMEZ
 * - Sadece rakip progress % görünür
 * - İlk tamamlayan kazanır
 */
@HiltViewModel
class PvpBlindRaceViewModel @Inject constructor(
    private val repository: PvpMatchRepository,
    private val auth: FirebaseAuth,
    private val hapticFeedback: HapticFeedback,
    private val soundEffects: SoundEffects,
    private val networkMonitor: NetworkMonitor,
    private val calculateMoveScoreUseCase: CalculateMoveScoreUseCase,
    private val checkCompletionBonusUseCase: CheckCompletionBonusUseCase,
    private val calculateFinalScoreUseCase: CalculateFinalScoreUseCase,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _gameState = MutableStateFlow(BlindRaceGameState())
    val gameState: StateFlow<BlindRaceGameState> = _gameState.asStateFlow()

    private val _matchData = MutableStateFlow<PvpMatch?>(null)
    val matchData: StateFlow<PvpMatch?> = _matchData.asStateFlow()
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Connected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // Scoring state
    private val _gameScore = MutableStateFlow(GameScore())
    val gameScore: StateFlow<GameScore> = _gameScore.asStateFlow()
    
    private val _bonusEvents = Channel<BonusEvent>(Channel.UNLIMITED)
    val bonusEvents: Flow<BonusEvent> = _bonusEvents.receiveAsFlow()
    
    // Completion tracking for bonuses
    private val completedBoxes = mutableSetOf<Int>()
    private val completedRows = mutableSetOf<Int>()
    private val completedColumns = mutableSetOf<Int>()

    private var matchId: String = ""
    private var hasLeftGame: Boolean = false  // Kullanıcı oyundan çıktı mı?
    private var hasSubmittedResult: Boolean = false
    private var isSubmittingResult: Boolean = false
    private var startTime: Long = 0
    private var timerJob: Job? = null
    private var progressSyncJob: Job? = null
    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // Move history for undo/redo
    private val moveHistory = mutableListOf<GameMove>()
    private var historyIndex = -1

    /**
     * Match'i başlat ve dinlemeye başla
     */
    fun initialize(matchId: String) {
        this.matchId = matchId
        
        viewModelScope.launch {
            // Presence sistemini başlat
            repository.startMatchPresence(matchId)
            
            // Rakibin presence'ını izle (DISCONNECT DETECTION)
            launch {
                delay(1000) // Match data yüklenmesini bekle
                val opponentId = _matchData.value?.getOpponentData(currentUserId)?.userId
                if (opponentId != null) {
                    android.util.Log.d("PvpBlindRace", "👀 Rakip presence dinlemeye başlandı: $opponentId")
                    
                    repository.observeOpponentPresence(matchId, opponentId).collectLatest { isOnline ->
                        android.util.Log.d("PvpBlindRace", "👥 Rakip durum: ${if (isOnline) "ONLINE ✅" else "OFFLINE ❌"}")
                        
                        if (!isOnline) {
                            // Rakip offline oldu - match'i iptal et
                            if (!_gameState.value.isFinished) {
                                android.util.Log.w("PvpBlindRace", "❌ Rakip offline - Match iptal ediliyor")
                                repository.cancelMatch(matchId, forfeitedByCurrentUser = false)
                            }
                        }
                    }
                } else {
                    android.util.Log.w("PvpBlindRace", "⚠️ Opponent ID bulunamadı")
                }
            }
            
            // Network durumunu izle
            launch {
                networkMonitor.observeConnectivity().collectLatest { status ->
                    when (status) {
                        is NetworkStatus.Connected -> {
                            if (_connectionState.value is ConnectionState.Disconnected) {
                                _connectionState.value = ConnectionState.Reconnecting
                                delay(1000) // 1 saniye bekle
                                _connectionState.value = ConnectionState.Connected
                                // Yeniden senkronize et
                                resyncGame()
                            }
                        }
                        is NetworkStatus.Disconnected -> {
                            _connectionState.value = ConnectionState.Disconnected
                        }
                    }
                }
            }
            
            // Match'i dinle
            launch {
                repository.observeMatch(matchId)
                    .catch { error ->
                        _gameState.value = _gameState.value.copy(
                            error = ErrorMessages.getErrorMessage(error)
                        )
                    }
                    .collectLatest { match ->
                        match?.let {
                            _matchData.value = it
                            updateFromMatch(it)
                            
                            // Match WAITING durumundaysa ve her iki oyuncu da READY ise - oyunu başlat
                            if (it.status == MatchStatus.WAITING && it.players.size == 2) {
                                val allReady = it.players.values.all { player -> 
                                    player.status == PlayerStatus.READY 
                                }
                                if (allReady) {
                                    android.util.Log.d("PvpBlindRace", "🚀 Her iki oyuncu hazır, match başlatılıyor...")
                                    repository.startMatch(matchId)
                                }
                            }
                            
                            // İlk kez IN_PROGRESS durumuna geçtiyse timer başlat
                            if (it.status == MatchStatus.IN_PROGRESS && startTime == 0L) {
                                startGame()
                            }
                            
                            // Match tamamlandıysa veya iptal edildiyse oyunu bitir
                            if (it.status == MatchStatus.COMPLETED || it.status == MatchStatus.CANCELLED) {
                                android.util.Log.d("PvpBlindRace", "🏁 Match durumu: ${it.status}")
                                if (it.status == MatchStatus.CANCELLED) {
                                    android.util.Log.w("PvpBlindRace", "❌ Match iptal edildi - Oyun sonlandırılıyor")
                                }
                                stopGame()
                            }
                        }
                    }
            }
        }
    }
    
    /**
     * Bağlantı yeniden kurulduğunda oyunu senkronize et
     */
    private fun resyncGame() {
        viewModelScope.launch {
            try {
                // Son progress'i gönder
                syncProgress()
                
                _gameState.value = _gameState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _gameState.value = _gameState.value.copy(
                    error = ErrorMessages.getErrorMessage(e)
                )
            }
        }
    }

    private fun startGame() {
        startTime = System.currentTimeMillis()
        hasSubmittedResult = false
        isSubmittingResult = false
        startTimer()
        startProgressSync()
        
        // Match status'ü güncelle
        viewModelScope.launch {
            repository.updatePlayerStatus(matchId, PlayerStatus.PLAYING)
        }
    }

    private fun stopGame() {
        timerJob?.cancel()
        progressSyncJob?.cancel()
        
        // Eğer gameState zaten isFinished=true ve isWinner/error set edilmişse, onları koru
        if (!_gameState.value.isFinished) {
            _gameState.value = _gameState.value.copy(
                isFinished = true
            )
        }
    }

    private fun updateFromMatch(match: PvpMatch) {
        android.util.Log.d("PvpBlindRace", "📥 updateFromMatch - Status: ${match.status}, WinnerId: ${match.winnerId}, isFinished: ${_gameState.value.isFinished}, hasLeftGame=$hasLeftGame")
        
        // Eğer oyun zaten bittiyse, başka güncelleme yapma!
        if (_gameState.value.isFinished) {
            android.util.Log.d("PvpBlindRace", "⏭️ Oyun zaten bitmiş, güncelleme atlanıyor")
            ensureFinalResultSubmitted(match)
            return
        }
        
        if (_gameState.value.grid.isEmpty()) {
            // İlk yükleme - grid'i oluştur
            val grid = parsePuzzleToGrid(match.puzzle.puzzleString)
            val solution = match.puzzle.solution
            val remaining = calculateRemainingNumbers(grid)
            
            _gameState.value = _gameState.value.copy(
                grid = grid,
                solution = solution,
                initialPuzzle = match.puzzle.puzzleString,
                remainingNumbers = remaining,
                resultMessage = null,
                error = null,
                isCancelled = false,
                isFinished = false
            )
        }
        
        // Rakip progress'ini güncelle
        val opponentData = match.getOpponentData(currentUserId)
        opponentData?.result?.let { result ->
            _gameState.value = _gameState.value.copy(
                opponentProgress = calculateProgressFromScore(result.score)
            )
        }
        
        // Match bittiğinde (COMPLETED veya CANCELLED)
        if (match.status == MatchStatus.COMPLETED || match.status == MatchStatus.CANCELLED) {
            val isCancelled = match.status == MatchStatus.CANCELLED
            
            if (isCancelled) {
                val winnerId = match.winnerId
                val isWinner = when {
                    winnerId != null -> winnerId == currentUserId
                    else -> !hasLeftGame
                }

                val resultMessage = if (isWinner) {
                    resourceProvider.getString(R.string.pvp_opponent_left_win)
                } else {
                    resourceProvider.getString(R.string.pvp_player_left_loss)
                }

                android.util.Log.w(
                    "PvpBlindRace",
                    "🏁 OYUN İPTAL EDİLDİ! isWinner=$isWinner (winnerId=$winnerId, hasLeftGame=$hasLeftGame)"
                )

                _gameState.value = _gameState.value.copy(
                    isFinished = true,
                    isWinner = isWinner,
                    isCancelled = true,  // İptal bayrağını set et
                    error = resultMessage,
                    resultMessage = resultMessage
                )
            } else {
                // COMPLETED: Normal bitiş - winnerId'ye bak
                val winnerId = match.winnerId
                val isWinner = winnerId == currentUserId
                
                android.util.Log.w("PvpBlindRace", "🏁 OYUN BİTTİ! isWinner=$isWinner, winnerId=$winnerId")
                
                _gameState.value = _gameState.value.copy(
                    isFinished = true,
                    isWinner = isWinner,
                    isCancelled = false,  // Normal bitiş
                    error = null,
                    resultMessage = null
                )
            }
            
            android.util.Log.d("PvpBlindRace", "✅ GameState güncellendi - error: ${_gameState.value.error}")

            ensureFinalResultSubmitted(match)
        }
    }

    private fun parsePuzzleToGrid(puzzleString: String): Array<Array<Cell>> {
        val grid = Array(9) { Array(9) { Cell() } }
        
        puzzleString.forEachIndexed { index, char ->
            val row = index / 9
            val col = index % 9
            
            if (char.isDigit() && char != '0') {
                val value = char.digitToInt()
                grid[row][col] = Cell(
                    value = value,
                    isInitial = true,
                    isError = false
                )
            } else {
                grid[row][col] = Cell(
                    value = 0,
                    isInitial = false,
                    isError = false
                )
            }
        }
        
        return grid
    }

    /**
     * Timer - Her saniye elapsed time'ı güncelle
     */
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            var secondsElapsed = 0
            while (true) {
                delay(1000)
                secondsElapsed++
                val elapsed = System.currentTimeMillis() - startTime
                _gameState.value = _gameState.value.copy(
                    elapsedTime = elapsed
                )
                
                // Her 5 saniyede bir heartbeat gönder
                if (secondsElapsed % 5 == 0) {
                    repository.updateHeartbeat(matchId)
                }
            }
        }
    }

    /**
     * Progress Sync - Her 3 saniyede bir Firebase'e progress gönder
     */
    private fun startProgressSync() {
        progressSyncJob = viewModelScope.launch {
            while (true) {
                delay(3000) // 3 saniye
                syncProgress()
            }
        }
    }

    private suspend fun syncProgress() {
        val currentState = _gameState.value
        val score = currentState.correctMoves
        val timeElapsed = System.currentTimeMillis() - startTime
        val accuracy = if (currentState.totalMoves > 0) {
            (currentState.correctMoves.toFloat() / currentState.totalMoves.toFloat()) * 100f
        } else {
            100f
        }
        
        android.util.Log.d("PvpBlindRace", "📤 Progress senkronize ediliyor: score=$score, accuracy=$accuracy%")
        
        val result = PlayerResult(
            completedAt = System.currentTimeMillis(),
            score = score,
            timeElapsed = timeElapsed,
            accuracy = accuracy
        )
        
        repository.submitPlayerResult(matchId, result).onSuccess {
            android.util.Log.d("PvpBlindRace", "✅ Progress başarıyla gönderildi")
        }.onFailure { error ->
            android.util.Log.e("PvpBlindRace", "❌ Progress gönderim hatası: ${error.message}")
        }
    }

    /**
     * Hücre seçimi
     */
    fun onCellSelected(row: Int, col: Int) {
        val currentState = _gameState.value
        val cell = currentState.grid[row][col]
        val displayValue = cell.value

        hapticFeedback.lightClick()

        // Hücreye tıklandığında:
        // 1. Hücreyi seç
        // 2. Eğer hücrede sayı varsa, o sayıyı highlight et
        // 3. showAffectedAreas = false (grid'den geldiği için sadece seçili hücrenin alanları gösterilecek)
        _gameState.value = currentState.copy(
            selectedCell = row to col,
            highlightedNumber = if (displayValue != 0) displayValue else null,
            showAffectedAreas = false, // Grid'den geldiği için false
            error = null
        )
    }
    
    /**
     * Note mode toggle
     */
    fun toggleNoteMode() {
        _gameState.value = _gameState.value.copy(
            isNoteMode = !_gameState.value.isNoteMode
        )
    }
    
    /**
     * Hint - Seçili hücreye doğru sayıyı koy
     */
    fun onHintRequested() {
        val currentState = _gameState.value
        if (currentState.hintsUsed >= 3) {
            hapticFeedback.error()
            _gameState.value = currentState.copy(
                error = resourceProvider.getString(R.string.error_max_hints_reached)
            )
            return
        }
        
        val (row, col) = currentState.selectedCell ?: run {
            hapticFeedback.error()
            _gameState.value = currentState.copy(
                error = resourceProvider.getString(R.string.error_select_cell)
            )
            return
        }
        
        val cell = currentState.grid[row][col]
        if (cell.isFixed || cell.value != 0) {
            hapticFeedback.error()
            _gameState.value = currentState.copy(
                error = resourceProvider.getString(R.string.error_hint_not_available)
            )
            return
        }
        
        // Doğru sayıyı bul
        val correctValue = currentState.solution[row * 9 + col].digitToInt()
        
        // Sayıyı koy
        placeNumber(row, col, correctValue)
        
        // Hint sayısını artır
        _gameState.value = _gameState.value.copy(
            hintsUsed = currentState.hintsUsed + 1
        )
        
        hapticFeedback.success()
        soundEffects.playHint()
    }

    /**
     * Sayı seçimi
     */
    fun onNumberSelected(number: Int) {
        val currentState = _gameState.value
        val selectedCellPos = currentState.selectedCell
        
        // Seçili hücre yoksa, sadece highlight yap (number pad'den geldiği için tüm alanları göster)
        if (selectedCellPos == null) {
            _gameState.value = currentState.copy(
                highlightedNumber = number,
                showAffectedAreas = true // Number pad'den geldiği için true
            )
            return
        }
        
        val (row, col) = selectedCellPos
        val cell = currentState.grid[row][col]
        
        // Başlangıç hücreleri değiştirilemez
        if (cell.isFixed) {
            // Initial hücreye tıklandıysa, sayıyı highlight et ve seçimi kaldır
            _gameState.value = currentState.copy(
                selectedCell = null,
                highlightedNumber = number,
                showAffectedAreas = true // Number pad'den geldiği için true
            )
            return
        }
        
        // EĞER HÜCRE DOLUYSA (kullanıcı tarafından yazılmış)
        if (cell.value != 0) {
            // Seçimi kaldır ve number pad'deki sayıyı highlight et
            _gameState.value = currentState.copy(
                selectedCell = null,
                highlightedNumber = number,
                showAffectedAreas = true // Number pad'den geldiği için true
            )
            return
        }
        
        // BURAYA GELDİYSEK HÜCRE BOŞ DEMEKTİR
        
        // Number pad'e tıklandığında o sayıyı highlight et
        _gameState.value = currentState.copy(
            highlightedNumber = number,
            showAffectedAreas = true // Number pad'den geldiği için true
        )
        
        // Note mode ise not ekle/çıkar
        if (currentState.isNoteMode) {
            val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
            val currentNotes = newGrid[row][col].notes.toMutableSet()
            
            if (currentNotes.contains(number)) {
                currentNotes.remove(number)
            } else {
                currentNotes.add(number)
            }
            
            newGrid[row][col] = newGrid[row][col].copy(
                notes = currentNotes
            )
            
            _gameState.value = currentState.copy(
                grid = newGrid
            )
            
            hapticFeedback.lightClick()
            return
        }
        
        // Sayıyı koy
        placeNumber(row, col, number)
    }

    private fun placeNumber(row: Int, col: Int, number: Int) {
        android.util.Log.d("PvpBlindRace", "🎯 placeNumber çağrıldı: ($row,$col) = $number")
        
        val currentState = _gameState.value
        val cell = currentState.grid[row][col]
        val oldValue = cell.value
        
        // ✅ DOĞRU HÜCRE KONTROLÜ - Hücre zaten doğru dolduysa hamle yapma
        val correctValue = currentState.solution[row * 9 + col].digitToInt()
        if (oldValue == correctValue) {
            android.util.Log.d("PvpBlindRace", "⚠️ Bu hücre zaten doğru dolu: ($row,$col) = $oldValue")
            hapticFeedback.error()
            return
        }
        
        // Undo için kaydet
        val move = GameMove(row, col, oldValue, number)
        addToHistory(move)
        
        // Grid'i güncelle
        val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
        newGrid[row][col] = newGrid[row][col].copy(
            value = number,
            notes = emptySet() // Sayı konulunca notları temizle
        )
        
        // Doğru mu kontrol et
        val isCorrect = number == correctValue
        
        // **SCORING SYSTEM** - Calculate score for this move
        val currentScore = _gameScore.value
        val (updatedScore, pointsEarned) = calculateMoveScoreUseCase(
            currentScore = currentScore,
            isCorrect = isCorrect,
            row = row,
            col = col,
            number = number
        )
        
        // Check for completion bonuses
        if (isCorrect) {
            val completionEvents = checkCompletionBonusUseCase(
                grid = newGrid,
                row = row,
                col = col,
                completedBoxes = completedBoxes,
                completedRows = completedRows,
                completedColumns = completedColumns
            )
            
            // Apply completion bonuses to score
            val finalScore = completionEvents.fold(updatedScore) { score, event ->
                score.copy(
                    finalScore = score.finalScore + event.bonusEarned,
                    completionBonuses = score.completionBonuses + event.bonusEarned,
                    boxesCompleted = if (event.type == CompletionType.BOX) 
                        score.boxesCompleted + 1 else score.boxesCompleted,
                    rowsCompleted = if (event.type == CompletionType.ROW)
                        score.rowsCompleted + 1 else score.rowsCompleted,
                    columnsCompleted = if (event.type == CompletionType.COLUMN)
                        score.columnsCompleted + 1 else score.columnsCompleted
                )
            }
            
            _gameScore.value = finalScore
            
            // Show bonus popups - Convert CompletionEvent to BonusEvent
            completionEvents.forEach { event ->
                val completionMessage = when (event.type) {
                    CompletionType.BOX -> resourceProvider.getString(R.string.bonus_box_complete)
                    CompletionType.ROW -> resourceProvider.getString(R.string.bonus_row_complete)
                    CompletionType.COLUMN -> resourceProvider.getString(R.string.bonus_column_complete)
                }
                val formattedCompletionMessage = resourceProvider.getString(
                    R.string.bonus_completion_popup,
                    completionMessage,
                    event.bonusEarned
                )
                viewModelScope.launch {
                    _bonusEvents.send(
                        BonusEvent(
                            type = BonusType.COMPLETION,
                            message = formattedCompletionMessage,
                            points = event.bonusEarned,
                            position = row to col
                        )
                    )
                }
            }
            
            // **FIRST FINISH BONUS** - Blind Race'de ilk bitiren extra bonus
            // This will be calculated in finishGame if player finishes first
        } else {
            _gameScore.value = updatedScore
        }
        
        // Show points earned popup
        if (pointsEarned > 0) {
            viewModelScope.launch {
                _bonusEvents.send(
                    BonusEvent(
                        type = if (isCorrect) BonusType.STREAK else BonusType.SPECIAL,
                        message = if (isCorrect) {
                            resourceProvider.getString(R.string.bonus_points_gain, pointsEarned)
                        } else {
                            resourceProvider.getString(R.string.bonus_points_loss, pointsEarned)
                        },
                        points = pointsEarned,
                        position = row to col
                    )
                )
            }
        }
        
        // Feedback
        if (isCorrect) {
            hapticFeedback.mediumClick()
            soundEffects.playClick()
        } else {
            hapticFeedback.error()
            soundEffects.playError()
        }
        
        // Conflict'leri bul
        val conflicts = findConflicts(newGrid, row, col)
        
        // Kalan sayıları hesapla
        val remaining = calculateRemainingNumbers(newGrid)
        
        // State güncelle
        val newCorrectMoves = if (isCorrect) currentState.correctMoves + 1 else currentState.correctMoves
        val newTotalMoves = currentState.totalMoves + 1
        val progress = calculateProgress(newGrid)
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            correctMoves = newCorrectMoves,
            totalMoves = newTotalMoves,
            myProgress = progress,
            conflictCells = conflicts,
            remainingNumbers = remaining,
            canUndo = true,
            canRedo = false
        )
        
        // Tamamlandı mı kontrol et
        if (isGridComplete(newGrid)) {
            finishGame()
        }
    }

    private fun finishGame() {
        hapticFeedback.success()
        soundEffects.playSuccess()
        
        val currentState = _gameState.value
        val elapsedMs = if (startTime != 0L) System.currentTimeMillis() - startTime else 0L
        
        // **FINAL SCORE CALCULATION**
        val matchData = _matchData.value
        val baseFinalScore = calculateFinalScoreUseCase(
            gameScore = _gameScore.value,
            elapsedTimeMs = elapsedMs,
            difficulty = matchData?.puzzle?.difficulty ?: "MEDIUM",
            usedNotes = false // Notes allowed in Blind Race but track separately
        )
        
        // **FIRST FINISH BONUS** - Check if this player finished first
        val opponentFinished = matchData?.players?.values?.any {
            it.userId != currentUserId && it.status == PlayerStatus.FINISHED
        } ?: false
        
        val isFirstFinish = !opponentFinished
        val firstFinishBonus = if (isFirstFinish) 5000 else 0
        
        val finalScoreWithRaceBonus = baseFinalScore.copy(
            finalScore = baseFinalScore.finalScore + firstFinishBonus,
            specialBonuses = baseFinalScore.specialBonuses + firstFinishBonus
        )
        
        _gameScore.value = finalScoreWithRaceBonus
        
        // Show first finish bonus popup
        if (isFirstFinish) {
            viewModelScope.launch {
                _bonusEvents.send(
                    BonusEvent(
                        type = BonusType.SPECIAL,
                        message = resourceProvider.getString(R.string.pvp_first_to_finish_bonus),
                        points = firstFinishBonus,
                        position = null
                    )
                )
            }
        }
        
        _gameState.value = currentState.copy(
            isFinished = true
        )
        
        // Final result'u gönder
        viewModelScope.launch {
            isSubmittingResult = true
            try {
                val result = buildPlayerResult(finalScoreWithRaceBonus, elapsedMs, isFirstFinish)
                val submitResult = repository.submitPlayerResult(matchId, result)
                if (submitResult.isSuccess) {
                    hasSubmittedResult = true
                    repository.updatePlayerStatus(matchId, PlayerStatus.FINISHED)
                } else {
                    hasSubmittedResult = false
                }
            } finally {
                isSubmittingResult = false
            }
        }
    }

    /**
     * Silme
     */
    fun erase() {
        val currentState = _gameState.value
        val (row, col) = currentState.selectedCell ?: return
        
        val cell = currentState.grid[row][col]
        if (cell.isFixed || cell.isEmpty) return
        
        placeNumber(row, col, 0)
        
        // Grid'i güncelle
        val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
        newGrid[row][col] = newGrid[row][col].copy(
            value = 0
        )
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            conflictCells = emptySet()
        )
    }

    private fun ensureFinalResultSubmitted(match: PvpMatch) {
        val myData = match.players[currentUserId] ?: return

        if (myData.result != null) {
            hasSubmittedResult = true
            return
        }

        if (hasSubmittedResult || isSubmittingResult) {
            return
        }

        val elapsedMs = if (startTime != 0L) System.currentTimeMillis() - startTime else 0L
        val baseFinalScore = calculateFinalScoreUseCase(
            gameScore = _gameScore.value,
            elapsedTimeMs = elapsedMs,
            difficulty = match.puzzle.difficulty,
            usedNotes = false
        )

        val opponentFinished = match.players.values.any {
            it.userId != currentUserId && it.status == PlayerStatus.FINISHED
        }
        val isFirstFinish = !opponentFinished && match.status == MatchStatus.COMPLETED
        val firstFinishBonus = if (isFirstFinish) 5000 else 0
        val finalScoreWithRaceBonus = baseFinalScore.copy(
            finalScore = baseFinalScore.finalScore + firstFinishBonus,
            specialBonuses = baseFinalScore.specialBonuses + firstFinishBonus
        )

        _gameScore.value = finalScoreWithRaceBonus

        viewModelScope.launch {
            isSubmittingResult = true
            try {
                val result = buildPlayerResult(finalScoreWithRaceBonus, elapsedMs, isFirstFinish)
                val submitResult = repository.submitPlayerResult(matchId, result)
                if (submitResult.isSuccess) {
                    hasSubmittedResult = true
                    repository.updatePlayerStatus(matchId, PlayerStatus.FINISHED)
                } else {
                    hasSubmittedResult = false
                }
            } finally {
                isSubmittingResult = false
            }
        }
    }

    private fun buildPlayerResult(finalScore: GameScore, elapsedMs: Long, isFirstFinish: Boolean): PlayerResult {
        return PlayerResult(
            completedAt = System.currentTimeMillis(),
            score = finalScore.finalScore,
            timeElapsed = elapsedMs,
            accuracy = finalScore.accuracy,
            finalScore = finalScore.finalScore,
            basePoints = finalScore.basePoints,
            streakBonus = finalScore.streakBonus,
            timeBonus = finalScore.timeBonus,
            completionBonuses = finalScore.completionBonuses,
            totalCompletionBonus = finalScore.completionBonuses,
            maxStreak = finalScore.maxStreak,
            totalMoves = finalScore.totalMoves,
            correctMoves = finalScore.correctMoves,
            wrongMoves = finalScore.wrongMoves,
            hintsUsed = finalScore.hintsUsed,
            isPerfectGame = finalScore.perfectGame,
            isFirstFinish = isFirstFinish
        )
    }

    /**
     * Undo/Redo
     */
    fun undo() {
        if (historyIndex < 0) return
        
        val move = moveHistory[historyIndex]
        historyIndex--
        
        val currentState = _gameState.value
        val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
        newGrid[move.row][move.col] = newGrid[move.row][move.col].copy(
            value = move.oldValue
        )
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            canUndo = historyIndex >= 0,
            canRedo = true,
            conflictCells = emptySet()
        )
    }

    fun redo() {
        if (historyIndex >= moveHistory.size - 1) return
        
        historyIndex++
        val move = moveHistory[historyIndex]
        
        val currentState = _gameState.value
        val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
        newGrid[move.row][move.col] = newGrid[move.row][move.col].copy(
            value = move.newValue
        )
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            canUndo = true,
            canRedo = historyIndex < moveHistory.size - 1,
            conflictCells = emptySet()
        )
    }

    private fun addToHistory(move: GameMove) {
        // Redo history'yi temizle
        if (historyIndex < moveHistory.size - 1) {
            moveHistory.subList(historyIndex + 1, moveHistory.size).clear()
        }
        
        moveHistory.add(move)
        historyIndex++
    }

    /**
     * Helper functions
     */
    private fun calculateProgress(grid: Array<Array<Cell>>): Float {
        val totalCells = 81
        val filledCorrectly = grid.flatten().count { !it.isEmpty && !it.isFixed }
        return (filledCorrectly.toFloat() / totalCells.toFloat()) * 100f
    }

    private fun calculateProgressFromScore(score: Int): Float {
        return (score.toFloat() / 81f) * 100f
    }

    private fun isGridComplete(grid: Array<Array<Cell>>): Boolean {
        return grid.flatten().all { !it.isEmpty }
    }
    
    private fun calculateRemainingNumbers(grid: Array<Array<Cell>>): Map<Int, Int> {
        val remaining = mutableMapOf<Int, Int>()
        
        // Her sayı için 9 kez kullanılabilir
        for (num in 1..9) {
            val count = grid.flatten().count { it.value == num }
            remaining[num] = (9 - count).coerceAtLeast(0)
        }
        
        return remaining
    }

    private fun findConflicts(grid: Array<Array<Cell>>, row: Int, col: Int): Set<Pair<Int, Int>> {
        val conflicts = mutableSetOf<Pair<Int, Int>>()
        val value = grid[row][col].value
        
        if (value == 0) return emptySet()
        
        // Row conflicts
        for (c in 0..8) {
            if (c != col && grid[row][c].value == value) {
                conflicts.add(row to c)
                conflicts.add(row to col)
            }
        }
        
        // Column conflicts
        for (r in 0..8) {
            if (r != row && grid[r][col].value == value) {
                conflicts.add(r to col)
                conflicts.add(row to col)
            }
        }
        
        // Box conflicts
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if ((r != row || c != col) && grid[r][c].value == value) {
                    conflicts.add(r to c)
                    conflicts.add(row to col)
                }
            }
        }
        
        return conflicts
    }

    /**
     * Oyundan ayrıl (forfeit)
     * Match'i cancel eder, rakip kazanır
     */
    fun leaveGame() {
        viewModelScope.launch {
            repository.stopMatchPresence(matchId)
            if (!_gameState.value.isFinished) {
                // ÖNCE flag'i set et (çıkan kullanıcı işareti)
                hasLeftGame = true
                
                // Oyuncu çıkıyor - SADECE overlay göster, isFinished=true YAPMA!
                // Firebase'den CANCELLED güncellemesi gelince isFinished=true olacak
                    _gameState.value = _gameState.value.copy(
                        isCancelled = true,  // Overlay gösterilsin
                        isWinner = false,
                        error = resourceProvider.getString(R.string.pvp_player_left_loss),
                        resultMessage = resourceProvider.getString(R.string.pvp_player_left_loss)
                )
                
                // Firebase'e de bildir (rakip kazanacak)
                repository.cancelMatch(matchId, forfeitedByCurrentUser = true)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        progressSyncJob?.cancel()
        viewModelScope.launch {
            repository.stopMatchPresence(matchId)
        }
        // ViewModel yok edilirken oyun bitmemişse - oyundan ayrıldı sayılır
        if (!_gameState.value.isFinished) {
            viewModelScope.launch {
                hasLeftGame = true
                repository.cancelMatch(matchId, forfeitedByCurrentUser = true)
            }
        }
    }
    
    /**
     * Hata mesajını temizle
     */
    fun clearError() {
        _gameState.value = _gameState.value.copy(error = null)
    }
}

/**
 * Game State
 */
data class BlindRaceGameState(
    val grid: Array<Array<Cell>> = emptyArray(),
    val solution: String = "",
    val initialPuzzle: String = "",
    val selectedCell: Pair<Int, Int>? = null,
    val highlightedNumber: Int? = null,
    val showAffectedAreas: Boolean = false, // Number pad'den mi grid'den mi geldiğini belirler
    val conflictCells: Set<Pair<Int, Int>> = emptySet(),
    val myProgress: Float = 0f,
    val opponentProgress: Float = 0f,
    val elapsedTime: Long = 0,
    val correctMoves: Int = 0,
    val totalMoves: Int = 0,
    val hintsUsed: Int = 0,
    val isNoteMode: Boolean = false,
    val remainingNumbers: Map<Int, Int> = emptyMap(),
    val isFinished: Boolean = false,
    val isWinner: Boolean? = null,
    val isCancelled: Boolean = false,  // Oyun iptal edildi mi (birisi çıktı)?
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val error: String? = null,
    val resultMessage: String? = null
)
