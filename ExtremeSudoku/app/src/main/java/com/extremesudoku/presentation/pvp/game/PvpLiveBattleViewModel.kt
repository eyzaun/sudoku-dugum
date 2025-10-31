package com.extremesudoku.presentation.pvp.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

/**
 * Live Battle Mode ViewModel
 * - Oyuncular birbirlerinin hareketlerini GERÇEK ZAMANLI görür
 * - Her move Firebase'e gönderilir ve rakip anında görür
 * - Kim daha fazla DOĞRU sayı koyarsa o kazanır
 * - 10 dakika süre limiti
 */
@HiltViewModel
class PvpLiveBattleViewModel @Inject constructor(
    private val repository: PvpMatchRepository,
    private val auth: FirebaseAuth,
    private val hapticFeedback: HapticFeedback,
    private val soundEffects: SoundEffects,
    private val networkMonitor: NetworkMonitor,
    private val calculateMoveScoreUseCase: CalculateMoveScoreUseCase,
    private val checkCompletionBonusUseCase: CheckCompletionBonusUseCase,
    private val calculateFinalScoreUseCase: CalculateFinalScoreUseCase
) : ViewModel() {

    private val _gameState = MutableStateFlow(LiveBattleGameState())
    val gameState: StateFlow<LiveBattleGameState> = _gameState.asStateFlow()

    private val _matchData = MutableStateFlow<PvpMatch?>(null)
    val matchData: StateFlow<PvpMatch?> = _matchData.asStateFlow()

    private val _opponentMoves = MutableStateFlow<List<PvpMove>>(emptyList())
    val opponentMoves: StateFlow<List<PvpMove>> = _opponentMoves.asStateFlow()
    
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
    private var startTime: Long = 0
    private var timerJob: Job? = null
    private var moveCounter = 0

    // Move history for undo/redo
    private val myMoveHistory = mutableListOf<GameMove>()
    private var historyIndex = -1

    // Time limit: 10 minutes
    private val TIME_LIMIT_MS = 10 * 60 * 1000L
    
    // Public userId for UI access
    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""
        
    /**
     * Match'i başlat ve dinlemeye başla
     */
    fun initialize(matchId: String) {
        this.matchId = matchId
        
        viewModelScope.launch {
            // Presence sistemi başlat
            repository.startMatchPresence(matchId)
            
            // Match'i dinle
            launch {
                repository.observeMatch(matchId).collectLatest { match ->
                    match?.let {
                        _matchData.value = it
                        updateFromMatch(it)
                        
                        // Match WAITING durumundaysa ve her iki oyuncu da READY ise - oyunu başlat
                        if (it.status == MatchStatus.WAITING && it.players.size == 2) {
                            val allReady = it.players.values.all { player -> 
                                player.status == PlayerStatus.READY 
                            }
                            if (allReady) {
                                android.util.Log.d("PvpLiveBattle", "🚀 Her iki oyuncu hazır, match başlatılıyor...")
                                repository.startMatch(matchId)
                            }
                        }
                        
                        // İlk kez IN_PROGRESS durumuna geçtiyse oyunu başlat
                        if (it.status == MatchStatus.IN_PROGRESS && startTime == 0L) {
                            startGame()
                        }
                        
                        // Match tamamlandıysa veya iptal edildiyse oyunu bitir
                        if (it.status == MatchStatus.COMPLETED || it.status == MatchStatus.CANCELLED) {
                            android.util.Log.d("PvpLiveBattle", "🏁 Match durumu: ${it.status}")
                            if (it.status == MatchStatus.CANCELLED) {
                                android.util.Log.w("PvpLiveBattle", "❌ Match iptal edildi - Oyun sonlandırılıyor")
                            }
                            stopGame()
                        }
                    }
                }
            }
            
            // Rakip hareketlerini dinle (REAL-TIME)
            launch {
                repository.observeMoves(matchId).collectLatest { allMoves ->
                    android.util.Log.d("PvpLiveBattle", "📡 observeMoves: ${allMoves.size} toplam hamle alındı")
                    
                    // Sadece rakibin hareketlerini filtrele
                    val opponentMoves = allMoves.filter { it.playerId != currentUserId }
                    android.util.Log.d("PvpLiveBattle", "📡 Rakip hamleleri: ${opponentMoves.size} hamle")
                    
                    _opponentMoves.value = opponentMoves
                    
                    // Rakip hareketlerini grid'e uygula
                    applyOpponentMovesToGrid(opponentMoves)
                }
            }
            
            // Rakip presence'ını dinle (DISCONNECT DETECTION)
            launch {
                delay(1000) // Match data yüklenmesini bekle
                val opponentId = _matchData.value?.getOpponentData(currentUserId)?.userId
                if (opponentId != null) {
                    android.util.Log.d("PvpLiveBattle", "👀 Rakip presence dinlemeye başlandı: $opponentId")
                    
                    repository.observeOpponentPresence(matchId, opponentId).collectLatest { isOnline ->
                        android.util.Log.d("PvpLiveBattle", "👥 Rakip durum: ${if (isOnline) "ONLINE ✅" else "OFFLINE ❌"}")
                        
                        if (!isOnline) {
                            // Rakip offline oldu - match'i iptal et
                            if (!_gameState.value.isFinished) {
                                android.util.Log.w("PvpLiveBattle", "❌ Rakip offline - Match iptal ediliyor")
                                repository.cancelMatch(matchId)
                            }
                        }
                    }
                } else {
                    android.util.Log.w("PvpLiveBattle", "⚠️ Opponent ID bulunamadı")
                }
            }
            
            // Network durumunu dinle
            launch {
                networkMonitor.observeConnectivity().collectLatest { status ->
                    when (status) {
                        is NetworkStatus.Connected -> {
                            if (_connectionState.value == ConnectionState.Disconnected) {
                                _connectionState.value = ConnectionState.Reconnecting
                                resyncGame()
                            } else {
                                _connectionState.value = ConnectionState.Connected
                            }
                        }
                        is NetworkStatus.Disconnected -> {
                            _connectionState.value = ConnectionState.Disconnected
                            _gameState.value = _gameState.value.copy(
                                error = "Bağlantı kesildi"
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Bağlantı tekrar kurulduğunda oyunu senkronize et
     */
    private suspend fun resyncGame() {
        try {
            // Match'i tekrar yükle
            repository.getMatch(matchId).onSuccess { match ->
                _matchData.value = match
                updateFromMatch(match)
            }
            
            _connectionState.value = ConnectionState.Connected
            _gameState.value = _gameState.value.copy(
                error = null
            )
        } catch (e: Exception) {
            _gameState.value = _gameState.value.copy(
                error = ErrorMessages.getErrorMessage(e)
            )
        }
    }

    private fun startGame() {
        startTime = System.currentTimeMillis()
        startTimer()
        
        // Match status'ü güncelle
        viewModelScope.launch {
            repository.updatePlayerStatus(matchId, PlayerStatus.PLAYING)
        }
    }

    private fun stopGame() {
        timerJob?.cancel()
        
        // Eğer gameState zaten isFinished=true ve isWinner/error set edilmişse, onları koru
        if (!_gameState.value.isFinished) {
            _gameState.value = _gameState.value.copy(
                isFinished = true
            )
        }
    }

    private fun updateFromMatch(match: PvpMatch) {
        android.util.Log.d("PvpLiveBattle", "📥 updateFromMatch - Status: ${match.status}, WinnerId: ${match.winnerId}, isFinished: ${_gameState.value.isFinished}, hasLeftGame=$hasLeftGame")
        
        // Eğer oyun zaten bittiyse, başka güncelleme yapma!
        if (_gameState.value.isFinished) {
            android.util.Log.d("PvpLiveBattle", "⏭️ Oyun zaten bitmiş, güncelleme atlanıyor")
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
                remainingNumbers = remaining
            )
        }
        
        // Skorları güncelle
        val myData = match.players[currentUserId]
        val opponentData = match.getOpponentData(currentUserId)
        
        myData?.result?.let { result ->
            _gameState.value = _gameState.value.copy(
                myScore = result.score
            )
        }
        
        opponentData?.result?.let { result ->
            _gameState.value = _gameState.value.copy(
                opponentScore = result.score
            )
        }
        
        // Match bittiğinde (COMPLETED veya CANCELLED)
        if (match.status == MatchStatus.COMPLETED || match.status == MatchStatus.CANCELLED) {
            val isCancelled = match.status == MatchStatus.CANCELLED
            
            if (isCancelled) {
                // CANCELLED: Eğer hasLeftGame=false ise, BU KULLANICI OYUNDA KALDI (kazandı)
                // Eğer hasLeftGame=true ise, BU KULLANICI ÇIKTI (kaybetti)
                val isWinner = !hasLeftGame
                
                android.util.Log.w("PvpLiveBattle", "🏁 OYUN İPTAL EDİLDİ! isWinner=$isWinner (hasLeftGame=$hasLeftGame)")
                
                _gameState.value = _gameState.value.copy(
                    isFinished = true,
                    isWinner = isWinner,
                    isCancelled = true,  // İptal bayrağını set et
                    error = if (isWinner) {
                        "Rakip oyundan ayrıldı. Kazandınız!"
                    } else {
                        "Oyundan ayrıldınız. Kaybettiniz!"
                    }
                )
            } else {
                // COMPLETED: Normal bitiş - winnerId'ye bak
                val winnerId = match.winnerId
                val isWinner = winnerId == currentUserId
                
                android.util.Log.w("PvpLiveBattle", "🏁 OYUN BİTTİ! isWinner=$isWinner, winnerId=$winnerId")
                
                _gameState.value = _gameState.value.copy(
                    isFinished = true,
                    isWinner = isWinner,
                    isCancelled = false,  // Normal bitiş
                    error = null
                )
            }
            
            android.util.Log.d("PvpLiveBattle", "✅ GameState güncellendi - error: ${_gameState.value.error}")
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
                    isInitial = true
                )
            } else {
                grid[row][col] = Cell(
                    value = 0,
                    isInitial = false
                )
            }
        }
        
        return grid
    }

    /**
     * Rakip hareketlerini grid'e uygula (görselleştirme için)
     */
    private fun applyOpponentMovesToGrid(opponentMoves: List<PvpMove>) {
        android.util.Log.d("PvpLiveBattle", "👁️ Rakip hamleleri güncelleniyor: ${opponentMoves.size} hamle")
        
        val currentState = _gameState.value
        
        // ✅ SADECE DOĞRU hamleleri göster - Güvenlik/Hile önleme
        // Yanlış hamleleri göstermek rakibe ipucu verir (o hücrenin doğru olduğunu düşünebilir)
        val opponentCells = opponentMoves
            .filter { it.isCorrect }  // Sadece doğru hamleleri göster
            .associate { (it.row to it.col) to it.value }
        
        android.util.Log.d("PvpLiveBattle", "👁️ Rakip doğru hamleleri: ${opponentCells.size} hücre")
        android.util.Log.d("PvpLiveBattle", "👁️ Rakip toplam hamle sayısı: ${opponentMoves.size} deneme")
        
        _gameState.value = currentState.copy(
            opponentCells = opponentCells
        )
    }

    /**
     * Timer - Her saniye elapsed time'ı güncelle + TIME LIMIT kontrolü
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
                
                // Her 5 saniyede heartbeat gönder
                if (secondsElapsed % 5 == 0) {
                    repository.updateHeartbeat(matchId)
                }
                
                // TIME LIMIT aşıldı mı?
                if (elapsed >= TIME_LIMIT_MS) {
                    timeUp()
                    break
                }
            }
        }
    }

    /**
     * Süre doldu - Oyunu bitir
     */
    private fun timeUp() {
        hapticFeedback.error()
        soundEffects.playError()
        
        finishGame(forced = true)
    }

    /**
     * Hücre seçimi
     */
    fun onCellSelected(row: Int, col: Int) {
        val currentState = _gameState.value
        val cell = currentState.grid[row][col]
        
        // Fixed hücre seçilemez
        if (cell.isFixed) {
            hapticFeedback.error()
            soundEffects.playError()
            _gameState.value = currentState.copy(
                error = "Bu hücre değiştirilemez!"
            )
            return
        }
        
        // Rakip hücresi seçilemez - FEEDBACK EKLE!
        if (currentState.opponentCells.containsKey(row to col)) {
            hapticFeedback.error()
            soundEffects.playError()
            _gameState.value = currentState.copy(
                error = "Rakip bu hücreyi zaten doldurdu!"
            )
            return
        }
        
        // Normal selection
        // Hücreye tıklandığında:
        // 1. Hücreyi seç
        // 2. Eğer hücrede sayı varsa, o sayıyı highlight et
        // 3. showAffectedAreas = false (grid'den geldiği için sadece seçili hücrenin alanları gösterilecek)
        hapticFeedback.lightClick()
        _gameState.value = currentState.copy(
            selectedCell = row to col,
            highlightedNumber = if (cell.value != 0) cell.value else null,
            showAffectedAreas = false // Grid'den geldiği için false
        )
    }

    /**
     * Sayı seçimi - LIVE BATTLE'da her move Firebase'e gönderilir
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
        
        // Rakip bu hücreyi zaten doldurmuş mu?
        if (currentState.opponentCells.containsKey(row to col)) {
            hapticFeedback.error()
            // Seçimi kaldır ve sayıyı highlight et
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
        
        // Sayıyı koy
        placeNumber(row, col, number)
    }

    private fun placeNumber(row: Int, col: Int, number: Int) {
        android.util.Log.d("PvpLiveBattle", "🎯 placeNumber çağrıldı: ($row,$col) = $number")
        
        val currentState = _gameState.value
        val cell = currentState.grid[row][col]
        val oldValue = cell.value
        
        // ✅ DOĞRU HÜCRE KONTROLÜ - Hücre zaten doğru dolduysa hamle yapma
        val correctValue = currentState.solution[row * 9 + col].digitToInt()
        if (oldValue == correctValue) {
            android.util.Log.d("PvpLiveBattle", "⚠️ Bu hücre zaten doğru dolu: ($row,$col) = $oldValue")
            hapticFeedback.error()
            return
        }
        
        // Undo için kaydet
        val move = GameMove(row, col, oldValue, number)
        addToHistory(move)
        
        // Grid'i güncelle
        val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
        newGrid[row][col] = newGrid[row][col].copy(
            value = number
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
                viewModelScope.launch {
                    _bonusEvents.send(
                        BonusEvent(
                            type = BonusType.COMPLETION,
                            message = when (event.type) {
                                CompletionType.BOX -> "BOX COMPLETE!"
                                CompletionType.ROW -> "ROW COMPLETE!"
                                CompletionType.COLUMN -> "COLUMN COMPLETE!"
                            },
                            points = event.bonusEarned,
                            position = row to col
                        )
                    )
                }
            }
        } else {
            _gameScore.value = updatedScore
        }
        
        // Show points earned popup
        if (pointsEarned > 0) {
            viewModelScope.launch {
                _bonusEvents.send(
                    BonusEvent(
                        type = if (isCorrect) BonusType.STREAK else BonusType.SPECIAL,
                        message = if (isCorrect) "+${pointsEarned}" else "${pointsEarned}",
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
        
        // Skor güncelle: Use new scoring system's final score
        val newScore = _gameScore.value.finalScore
        
        // State güncelle
        _gameState.value = currentState.copy(
            grid = newGrid,
            myScore = newScore,
            totalMoves = currentState.totalMoves + 1,
            conflictCells = conflicts,
            remainingNumbers = remaining,
            canUndo = true,
            canRedo = false
        )
        
        // Move'u Firebase'e gönder (REAL-TIME)
        submitMoveToFirebase(row, col, number, isCorrect)
        
        // Oyun tamamlandı mı kontrol et (81 hücre dolu)
        if (isGridComplete(newGrid)) {
            finishGame(forced = false)
        }
    }

    /**
     * Move'u Firebase'e gönder - Rakip anında görecek
     */
    private fun submitMoveToFirebase(row: Int, col: Int, value: Int, isCorrect: Boolean) {
        android.util.Log.d("PvpLiveBattle", "📤 Move Firebase'e gönderiliyor: ($row,$col)=$value, doğru=$isCorrect")
        
        val move = PvpMove(
            playerId = currentUserId,
            timestamp = System.currentTimeMillis(),
            row = row,
            col = col,
            value = value,
            isCorrect = isCorrect,
            moveNumber = ++moveCounter
        )
        
        viewModelScope.launch {
            repository.submitMove(matchId, move).onSuccess {
                android.util.Log.d("PvpLiveBattle", "✅ Move Firebase'e başarıyla gönderildi")
            }.onFailure { error ->
                android.util.Log.e("PvpLiveBattle", "❌ Move gönderim hatası: ${error.message}")
            }
            
            // Skorumuzu güncelle
            syncScore()
        }
    }

    /**
     * Skorumuzu Firebase'e senkronize et
     */
    private suspend fun syncScore() {
        val currentState = _gameState.value
        val timeElapsed = System.currentTimeMillis() - startTime
        val accuracy = if (moveCounter > 0) {
            (currentState.myScore.toFloat() / moveCounter.toFloat()) * 100f
        } else {
            100f
        }
        
        val result = PlayerResult(
            completedAt = System.currentTimeMillis(),
            score = currentState.myScore,
            timeElapsed = timeElapsed,
            accuracy = accuracy
        )
        
        repository.submitPlayerResult(matchId, result)
    }

    /**
     * Oyunu bitir
     */
    private fun finishGame(forced: Boolean) {
        if (forced) {
            // Süre doldu
            hapticFeedback.error()
        } else {
            // Normal bitiş
            hapticFeedback.success()
            soundEffects.playSuccess()
        }
        
        val currentState = _gameState.value
        val timeElapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        
        // **FINAL SCORE CALCULATION**
        val matchData = _matchData.value
        val finalScore = calculateFinalScoreUseCase(
            gameScore = _gameScore.value,
            elapsedTimeMs = timeElapsed * 1000L,
            difficulty = matchData?.puzzle?.difficulty ?: "MEDIUM",
            usedNotes = false // PVP'de notes disabled
        )
        
        _gameScore.value = finalScore
        
        val accuracy = finalScore.accuracy
        
        _gameState.value = currentState.copy(
            isFinished = true,
            myScore = finalScore.finalScore
        )
        
        // Final result'u gönder
        viewModelScope.launch {
            val result = PlayerResult(
                completedAt = System.currentTimeMillis(),
                score = finalScore.finalScore,
                timeElapsed = (timeElapsed * 1000L), // Convert back to milliseconds
                accuracy = accuracy,
                // Enhanced scoring details
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
                isPerfectGame = finalScore.perfectGame
            )
            
            repository.submitPlayerResult(matchId, result)
            repository.updatePlayerStatus(matchId, PlayerStatus.FINISHED)
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
        
        // Rakip hücresi silinmez
        if (currentState.opponentCells.containsKey(row to col)) return
        
        // Grid'i güncelle
        val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
        newGrid[row][col] = newGrid[row][col].copy(
            value = 0
        )
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            conflictCells = emptySet()
        )
        
        hapticFeedback.lightClick()
    }

    /**
     * Undo/Redo
     */
    fun undo() {
        if (historyIndex < 0) return
        
        val move = myMoveHistory[historyIndex]
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
        
        hapticFeedback.lightClick()
    }

    fun redo() {
        if (historyIndex >= myMoveHistory.size - 1) return
        
        historyIndex++
        val move = myMoveHistory[historyIndex]
        
        val currentState = _gameState.value
        val newGrid = currentState.grid.map { it.clone() }.toTypedArray()
        newGrid[move.row][move.col] = newGrid[move.row][move.col].copy(
            value = move.newValue
        )
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            canUndo = true,
            canRedo = historyIndex < myMoveHistory.size - 1,
            conflictCells = emptySet()
        )
        
        hapticFeedback.lightClick()
    }

    private fun addToHistory(move: GameMove) {
        // Redo history'yi temizle
        if (historyIndex < myMoveHistory.size - 1) {
            myMoveHistory.subList(historyIndex + 1, myMoveHistory.size).clear()
        }
        
        myMoveHistory.add(move)
        historyIndex++
    }

    /**
     * Helper functions
     */
    private fun isGridComplete(grid: Array<Array<Cell>>): Boolean {
        return grid.flatten().all { !it.isEmpty }
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
     * Kalan sayıları hesapla (1-9 arası her sayıdan kaç tane kaldı)
     */
    private fun calculateRemainingNumbers(grid: Array<Array<Cell>>): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        for (i in 1..9) {
            val count = grid.flatten().count { it.value == i }
            counts[i] = 9 - count // Her sayıdan 9 olmalı
        }
        return counts
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
                    error = "Oyundan ayrıldınız. Kaybettiniz!"
                )
                
                // Firebase'e de bildir (rakip kazanacak)
                repository.cancelMatch(matchId)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        viewModelScope.launch {
            repository.stopMatchPresence(matchId)
        }
        // ViewModel yok edilirken oyun bitmemişse - oyundan ayrıldı sayılır
        if (!_gameState.value.isFinished) {
            viewModelScope.launch {
                repository.cancelMatch(matchId)
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
 * Live Battle Game State
 */
data class LiveBattleGameState(
    val grid: Array<Array<Cell>> = emptyArray(),
    val solution: String = "",
    val initialPuzzle: String = "",
    val selectedCell: Pair<Int, Int>? = null,
    val highlightedNumber: Int? = null,
    val showAffectedAreas: Boolean = false, // Number pad'den mi grid'den mi geldiğini belirler
    val conflictCells: Set<Pair<Int, Int>> = emptySet(),
    val opponentCells: Map<Pair<Int, Int>, Int> = emptyMap(), // Rakip hücreleri
    val remainingNumbers: Map<Int, Int> = (1..9).associateWith { 9 }, // Kaç sayı kaldı
    val myScore: Int = 0,           // Benim doğru sayı sayım
    val opponentScore: Int = 0,     // Rakip doğru sayı sayımı
    val totalMoves: Int = 0,        // Toplam hamle sayısı
    val elapsedTime: Long = 0,
    val isFinished: Boolean = false,
    val isWinner: Boolean? = null,
    val isCancelled: Boolean = false,  // Oyun iptal edildi mi (birisi çıktı)?
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val error: String? = null
)
