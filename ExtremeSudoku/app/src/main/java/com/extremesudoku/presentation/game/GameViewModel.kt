package com.extremesudoku.presentation.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.extremesudoku.R
import com.extremesudoku.data.models.Cell
import com.extremesudoku.data.models.GameState
import com.extremesudoku.data.models.Sudoku
import com.extremesudoku.data.models.scoring.BonusEvent
import com.extremesudoku.data.models.scoring.BonusType
import com.extremesudoku.data.models.scoring.CompletionEvent
import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.data.models.scoring.ScoringConstants
import com.extremesudoku.data.models.scoring.toJsonString
import com.extremesudoku.domain.usecase.*
import com.extremesudoku.domain.usecase.scoring.*
import com.extremesudoku.utils.Constants.MAX_HINTS
import com.extremesudoku.utils.HapticFeedback
import com.extremesudoku.utils.SoundEffects
import com.extremesudoku.utils.ResourceProvider
import com.extremesudoku.utils.applyNotesFromJson
import com.extremesudoku.utils.notesToJson
import com.extremesudoku.utils.toCellGrid
import com.extremesudoku.utils.toGridString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getSudokuUseCase: GetSudokuUseCase,
    private val validateMoveUseCase: ValidateMoveUseCase,
    private val getHintUseCase: GetHintUseCase,
    private val saveGameStateUseCase: SaveGameStateUseCase,
    private val checkCompletionUseCase: CheckCompletionUseCase,
    private val getGameStateUseCase: GetGameStateUseCase,
    private val hapticFeedback: HapticFeedback,
    private val soundEffects: SoundEffects,
    private val auth: com.google.firebase.auth.FirebaseAuth,
    // PUANLAMA SİSTEMİ USE CASES
    private val calculateMoveScoreUseCase: CalculateMoveScoreUseCase,
    private val calculatePenaltyUseCase: CalculatePenaltyUseCase,
    private val checkCompletionBonusUseCase: CheckCompletionBonusUseCase,
    private val calculateFinalScoreUseCase: CalculateFinalScoreUseCase,
    private val resourceProvider: ResourceProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "guest"
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    // PUANLAMA SİSTEMİ STATE
    private val _gameScore = MutableStateFlow(GameScore())
    val gameScore: StateFlow<GameScore> = _gameScore.asStateFlow()
    
    private val _bonusEvents = MutableStateFlow<List<BonusEvent>>(emptyList())
    val bonusEvents: StateFlow<List<BonusEvent>> = _bonusEvents.asStateFlow()
    
    private var timer: Job? = null
    private val moveHistory = mutableListOf<Move>()
    private var redoStack = mutableListOf<Move>()
    
    // TAMAMLAMA TAKİBİ (Completion tracking)
    private val completedBoxes = mutableSetOf<Int>()
    private val completedRows = mutableSetOf<Int>()
    private val completedColumns = mutableSetOf<Int>()
    
    // NOT KULLANIMI TAKİBİ
    private var hasUsedNotes = false
    
    init {
        // Route'a göre sudokuId veya gameId gelir
        val sudokuId = savedStateHandle.get<String>("sudokuId")  // YENİ OYUN
        val gameId = savedStateHandle.get<String>("gameId")      // DEVAM EDEN OYUN
        
        if (sudokuId != null) {
            // YENİ OYUN - Unique gameId oluştur ve başlat
            val uniqueGameId = "${sudokuId}_${System.currentTimeMillis()}_${currentUserId}"
            loadNewGame(sudokuId, uniqueGameId)
        } else if (gameId != null) {
            // DEVAM EDEN OYUN - Kayıtlı durumu yükle
            loadSavedGame(gameId)
        } else {
            // Fallback - hata durumu
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = resourceProvider.getString(R.string.error_game_parameters)
                )
            }
        }
    }
    
    /**
     * YENİ OYUN YÜKLEME
     * @param sudokuId Firebase'den gelen puzzle ID
     * @param uniqueGameId Unique oluşturulmuş game ID
     */
    private fun loadNewGame(sudokuId: String, uniqueGameId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = getSudokuUseCase(sudokuId)
            result.onSuccess { sudoku ->
                val grid = sudoku.puzzle.toCellGrid()
                resetCompletionTracking(grid)
                moveHistory.clear()
                redoStack.clear()
                _bonusEvents.value = emptyList()
                hasUsedNotes = false
                val multiplier = ScoringConstants.getDifficultyMultiplier(sudoku.difficulty)
                _gameScore.value = GameScore(
                    difficultyMultiplier = multiplier,
                    difficulty = sudoku.difficulty
                )
                
                _uiState.update {
                    it.copy(
                        sudoku = sudoku,
                        gameId = uniqueGameId,  // ← Unique gameId kaydet
                        currentGrid = grid,
                        initialGrid = grid,
                        remainingNumbers = calculateRemainingNumbers(grid),
                        isLoading = false
                    )
                }
                startTimer()
            }.onFailure { error ->
                val message = error.message ?: resourceProvider.getString(R.string.error_loading)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = message
                    )
                }
            }
        }
    }
    
    /**
     * KAYITLI OYUN YÜKLEME
     * @param gameId Database'de kayıtlı olan game ID
     */
    private fun loadSavedGame(gameId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Önce saved state'i al
            val savedState = getGameStateUseCase(gameId)
            
            if (savedState == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = resourceProvider.getString(R.string.error_game_not_found)
                    )
                }
                return@launch
            }
            
            // Sonra sudoku puzzle'ı al
            val result = getSudokuUseCase(savedState.sudokuId)
            result.onSuccess { sudoku ->
                // İlk olarak initial grid'i oluştur (puzzle'dan)
                val initialGrid = sudoku.puzzle.toCellGrid()
                
                // Saved state'i cell grid'e çevir (tüm sayılar isInitial=false olacak)
                val tempGrid = Array(9) { row ->
                    Array(9) { col ->
                        val index = row * 9 + col
                        val value = savedState.currentState.getOrNull(index)?.toString()?.toIntOrNull() ?: 0
                        Cell(value = value, isInitial = false)
                    }
                }
                
                // Şimdi initial grid'deki değerleri kontrol et ve isInitial flag'ini düzelt
                val grid = Array(9) { row ->
                    Array(9) { col ->
                        val initialValue = initialGrid[row][col].value
                        // Eğer bu hücre initial grid'de doluysa, isInitial = true yap
                        tempGrid[row][col].copy(
                            isInitial = initialValue != 0
                        )
                    }
                }
                
                // Notları da yükle
                val gridWithNotes = if (savedState.notes.isNotBlank()) {
                    grid.applyNotesFromJson(savedState.notes)
                } else grid

                resetCompletionTracking(gridWithNotes)
                moveHistory.clear()
                redoStack.clear()
                _bonusEvents.value = emptyList()
                hasUsedNotes = savedState.notes.isNotBlank()

                val rawScore = GameScore.fromJsonString(savedState.scoreDetails)
                val multiplier = ScoringConstants.getDifficultyMultiplier(sudoku.difficulty)
                val restoredScore = rawScore.copy(
                    finalScore = if (savedState.score != 0) savedState.score else rawScore.finalScore,
                    difficulty = sudoku.difficulty,
                    difficultyMultiplier = multiplier,
                    elapsedTimeMs = savedState.elapsedTime * 1000,
                    totalMoves = if (rawScore.totalMoves != 0) rawScore.totalMoves else savedState.moves,
                    hintsUsed = savedState.hintsUsed
                )
                _gameScore.value = restoredScore

                val isCompleted = savedState.isCompleted ||
                    checkCompletionUseCase(savedState.currentState, sudoku.solution)
                
                _uiState.update {
                    it.copy(
                        sudoku = sudoku,
                        gameId = gameId,  // ← Saved gameId kullan
                        currentGrid = gridWithNotes,
                        initialGrid = initialGrid,
                        remainingNumbers = calculateRemainingNumbers(gridWithNotes),
                        elapsedTime = savedState.elapsedTime,
                        moves = savedState.moves,
                        hintsUsed = savedState.hintsUsed,
                        createdAt = savedState.createdAt,
                        isCompleted = isCompleted,
                        isLoading = false
                    )
                }

                if (isCompleted) {
                    stopTimer()
                    if (!savedState.isCompleted) {
                        val completedState = savedState.copy(
                            isCompleted = true,
                            lastPlayedAt = System.currentTimeMillis()
                        )
                        saveGameStateUseCase(completedState)
                    }
                } else {
                    startTimer()
                }
            }.onFailure { error ->
                val message = error.message ?: resourceProvider.getString(R.string.error_loading)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = message
                    )
                }
            }
        }
    }
    
    fun onCellSelected(row: Int, col: Int) {
        val cell = _uiState.value.currentGrid[row][col]
        
        hapticFeedback.lightClick()
        
        // Hücreye tıklandığında:
        // 1. Hücreyi seç
        // 2. Eğer hücrede sayı varsa, o sayıyı highlight et
        // 3. showAffectedAreas = false (grid'den geldiği için sadece seçili hücrenin alanları gösterilecek)
        _uiState.update {
            it.copy(
                selectedCell = Pair(row, col),
                highlightedNumber = if (cell.value != 0) cell.value else null,
                showAffectedAreas = false // Grid'den geldiği için false
            )
        }
    }
    
    fun onNumberSelected(number: Int) {
        val selectedCellPos = _uiState.value.selectedCell
        
        // Seçili hücre yoksa, sadece highlight yap (number pad'den geldiği için tüm alanları göster)
        if (selectedCellPos == null) {
            _uiState.update {
                it.copy(
                    highlightedNumber = number,
                    showAffectedAreas = true // Number pad'den geldiği için true
                )
            }
            return
        }
        
        val (row, col) = selectedCellPos
        val currentCell = _uiState.value.currentGrid[row][col]
        
        // Başlangıç hücreleri değiştirilemez
        if (currentCell.isInitial) {
            // Initial hücreye tıklandıysa, sayıyı highlight et ve seçimi kaldır
            _uiState.update {
                it.copy(
                    selectedCell = null,
                    highlightedNumber = number,
                    showAffectedAreas = true // Number pad'den geldiği için true
                )
            }
            return
        }
        
        // EĞER HÜCRE DOLUYSA (kullanıcı tarafından yazılmış)
        if (currentCell.value != 0) {
            // Seçimi kaldır ve number pad'deki sayıyı highlight et
            _uiState.update {
                it.copy(
                    selectedCell = null,
                    highlightedNumber = number,
                    showAffectedAreas = true // Number pad'den geldiği için true
                )
            }
            return
        }
        
        // BURAYA GELDİYSEK HÜCRE BOŞ DEMEKTİR
        
        // Number pad'e tıklandığında o sayıyı highlight et
        _uiState.update {
            it.copy(
                highlightedNumber = number,
                showAffectedAreas = true // Number pad'den geldiği için true
            )
        }
        
        // Not modu aktifse
        if (_uiState.value.isNoteMode) {
            updateNotes(row, col, number)
            return
        }
        
        // Hareket geçmişine ekle
        val move = Move(row, col, currentCell.value, number)
        moveHistory.add(move)
        redoStack.clear()
        
        // Grid'i güncelle
        val newGrid = _uiState.value.currentGrid.map { it.clone() }.toTypedArray()
        
        // Doğruluk kontrolü - Solution'dan kontrol et
        val sudoku = _uiState.value.sudoku
        val isCorrect = if (sudoku != null && number != 0) {
            val solutionIndex = row * 9 + col
            val correctValue = sudoku.solution.getOrNull(solutionIndex)?.digitToInt() ?: 0
            number == correctValue
        } else {
            false
        }
        
        newGrid[row][col] = currentCell.copy(
            value = number,
            isError = !isCorrect && number != 0,
            notes = emptySet()
        )
        
        // ═══════════════════════════════════════════════════════════
        // PUANLAMA SİSTEMİ - HAMLE PUANI HESAPLA
        // ═══════════════════════════════════════════════════════════
        if (number != 0) {
            val (updatedScore, pointsEarned) = calculateMoveScoreUseCase(
                currentScore = _gameScore.value,
                isCorrect = isCorrect,
                row = row,
                col = col,
                number = number
            )
            
            _gameScore.value = updatedScore
            
            // Bonus popup göster (pozitif puan için)
            if (pointsEarned > 0) {
                showBonusPopup(
                    message = resourceProvider.getString(R.string.bonus_points_gain, pointsEarned),
                    points = pointsEarned,
                    position = row to col,
                    type = BonusType.STREAK
                )
            }
            
            // TAMAMLANMA BONUSLARI KONTROL ET
            val completionEvents = checkCompletionBonusUseCase(
                grid = newGrid,
                row = row,
                col = col,
                completedBoxes = completedBoxes,
                completedRows = completedRows,
                completedColumns = completedColumns
            )
            
            // Tamamlama event'lerini işle
            completionEvents.forEach { event ->
                // Tamamlanan bölgeyi kaydet
                when (event.type) {
                    com.extremesudoku.data.models.scoring.CompletionType.BOX -> completedBoxes.add(event.index)
                    com.extremesudoku.data.models.scoring.CompletionType.ROW -> completedRows.add(event.index)
                    com.extremesudoku.data.models.scoring.CompletionType.COLUMN -> completedColumns.add(event.index)
                }
                
                // Puana ekle
                val newCompletionBonuses = _gameScore.value.completionBonuses + event.bonusEarned
                val newBoxes = if (event.type == com.extremesudoku.data.models.scoring.CompletionType.BOX) 
                    _gameScore.value.boxesCompleted + 1 else _gameScore.value.boxesCompleted
                val newRows = if (event.type == com.extremesudoku.data.models.scoring.CompletionType.ROW) 
                    _gameScore.value.rowsCompleted + 1 else _gameScore.value.rowsCompleted
                val newColumns = if (event.type == com.extremesudoku.data.models.scoring.CompletionType.COLUMN) 
                    _gameScore.value.columnsCompleted + 1 else _gameScore.value.columnsCompleted
                
                // FINAL SCORE HESAPLA
                val newFinalScore = ((_gameScore.value.basePoints + _gameScore.value.streakBonus + 
                                     _gameScore.value.timeBonus + newCompletionBonuses + 
                                     _gameScore.value.specialBonuses - _gameScore.value.penalties) * 
                                     _gameScore.value.difficultyMultiplier).toInt()
                
                _gameScore.value = _gameScore.value.copy(
                    finalScore = newFinalScore,  // FIX: finalScore'u hesapla!
                    completionBonuses = newCompletionBonuses,
                    boxesCompleted = newBoxes,
                    rowsCompleted = newRows,
                    columnsCompleted = newColumns
                )
                
                // Bonus popup göster
                val baseMessage = when (event.type) {
                    com.extremesudoku.data.models.scoring.CompletionType.BOX -> resourceProvider.getString(R.string.bonus_box_complete)
                    com.extremesudoku.data.models.scoring.CompletionType.ROW -> resourceProvider.getString(R.string.bonus_row_complete)
                    com.extremesudoku.data.models.scoring.CompletionType.COLUMN -> resourceProvider.getString(R.string.bonus_column_complete)
                }
                val formattedMessage = resourceProvider.getString(
                    R.string.bonus_completion_popup,
                    baseMessage,
                    event.bonusEarned
                )
                showBonusPopup(
                    message = formattedMessage,
                    points = event.bonusEarned,
                    position = null,
                    type = BonusType.COMPLETION
                )
                
                // Özel ses efekti
                soundEffects.playSuccess()
            }
        }
        
        // Feedback
        if (!isCorrect && number != 0) {
            hapticFeedback.error()
            soundEffects.playError()
        } else if (number != 0) {
            hapticFeedback.mediumClick()
            soundEffects.playClick()
        }
        
        _uiState.update {
            it.copy(
                currentGrid = newGrid,
                moves = it.moves + 1,
                highlightedNumber = number,
                canUndo = moveHistory.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                remainingNumbers = calculateRemainingNumbers(newGrid),
                conflictCells = findConflicts(newGrid)
            )
        }
        
        // Tamamlanma kontrolü
        checkCompletion()
        
        // Otomatik kayıt
        autoSave()
    }
    
    private fun findConflicts(grid: Array<Array<Cell>>): Set<Pair<Int, Int>> {
        val conflicts = mutableSetOf<Pair<Int, Int>>()
        val isXSudoku = _uiState.value.sudoku?.isXSudoku ?: false
        
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = grid[row][col]
                if (cell.value == 0 || cell.isInitial) continue
                
                // Row conflicts
                for (c in 0..8) {
                    if (c != col && grid[row][c].value == cell.value) {
                        conflicts.add(Pair(row, col))
                        conflicts.add(Pair(row, c))
                    }
                }
                
                // Column conflicts
                for (r in 0..8) {
                    if (r != row && grid[r][col].value == cell.value) {
                        conflicts.add(Pair(row, col))
                        conflicts.add(Pair(r, col))
                    }
                }
                
                // Box conflicts
                val boxRow = (row / 3) * 3
                val boxCol = (col / 3) * 3
                for (r in boxRow until boxRow + 3) {
                    for (c in boxCol until boxCol + 3) {
                        if ((r != row || c != col) && grid[r][c].value == cell.value) {
                            conflicts.add(Pair(row, col))
                            conflicts.add(Pair(r, c))
                        }
                    }
                }
                
                // Diagonal conflicts (X-Sudoku)
                if (isXSudoku) {
                    // Main diagonal
                    if (row == col) {
                        for (i in 0..8) {
                            if (i != row && grid[i][i].value == cell.value) {
                                conflicts.add(Pair(row, col))
                                conflicts.add(Pair(i, i))
                            }
                        }
                    }
                    // Anti-diagonal
                    if (row + col == 8) {
                        for (i in 0..8) {
                            val j = 8 - i
                            if (i != row && grid[i][j].value == cell.value) {
                                conflicts.add(Pair(row, col))
                                conflicts.add(Pair(i, j))
                            }
                        }
                    }
                }
            }
        }
        
        return conflicts
    }
    
    private fun calculateRemainingNumbers(grid: Array<Array<Cell>>): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        for (i in 1..9) {
            val count = grid.flatten().count { it.value == i }
            counts[i] = 9 - count // Her sayıdan 9 olmalı
        }
        return counts
    }

    private fun resetCompletionTracking(grid: Array<Array<Cell>>) {
        completedBoxes.clear()
        completedRows.clear()
        completedColumns.clear()

        for (index in 0..8) {
            if ((0..8).all { col -> grid[index][col].value != 0 }) {
                completedRows.add(index)
            }
            if ((0..8).all { row -> grid[row][index].value != 0 }) {
                completedColumns.add(index)
            }
        }

        for (box in 0..8) {
            val startRow = (box / 3) * 3
            val startCol = (box % 3) * 3
            var isComplete = true
            for (row in startRow until startRow + 3) {
                for (col in startCol until startCol + 3) {
                    if (grid[row][col].value == 0) {
                        isComplete = false
                        break
                    }
                }
                if (!isComplete) break
            }
            if (isComplete) {
                completedBoxes.add(box)
            }
        }
    }
    
    fun onDeletePressed() {
        val selectedCellPos = _uiState.value.selectedCell ?: return
        val (row, col) = selectedCellPos
        val currentCell = _uiState.value.currentGrid[row][col]
        
        // Başlangıç hücreleri silinemez
        if (currentCell.isInitial) return
        
        // Boş hücreyi silmeye gerek yok
        if (currentCell.value == 0) return
        
        // Hareket geçmişine ekle
        val move = Move(row, col, currentCell.value, 0)
        moveHistory.add(move)
        redoStack.clear()
        
        // Grid'i güncelle
        val newGrid = _uiState.value.currentGrid.map { it.clone() }.toTypedArray()
        newGrid[row][col] = currentCell.copy(
            value = 0,
            isError = false,
            notes = emptySet()
        )
        
        // ═══════════════════════════════════════════════════════════
        // PUANLAMA SİSTEMİ - SİLME CEZASI (isteğe bağlı)
        // ═══════════════════════════════════════════════════════════
        // Şimdilik ceza yok, ama eklenebilir
        
        // Feedback
        hapticFeedback.lightClick()
        soundEffects.playClick()
        
        _uiState.update {
            it.copy(
                currentGrid = newGrid,
                highlightedNumber = null, // Silince highlight kaldır
                canUndo = moveHistory.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                remainingNumbers = calculateRemainingNumbers(newGrid),
                conflictCells = findConflicts(newGrid)
            )
        }
        
        // Otomatik kayıt
        autoSave()
    }
    
    fun onHintRequested() {
        if (_uiState.value.hintsUsed >= MAX_HINTS) {
            _uiState.update { it.copy(error = resourceProvider.getString(R.string.error_max_hints_reached)) }
            return
        }
        
        val currentState = _uiState.value.currentGrid.toGridString()
        val solution = _uiState.value.sudoku?.solution ?: return
        
        val hint = getHintUseCase(currentState, solution) ?: return
        val (row, col) = hint
        val correctNumber = solution[row * 9 + col].toString().toInt()
        
        // ═══════════════════════════════════════════════════════════
        // PUANLAMA SİSTEMİ - HİNT CEZASI
        // ═══════════════════════════════════════════════════════════
        val penalizedScore = calculatePenaltyUseCase.calculateHintPenalty(_gameScore.value)
        _gameScore.value = penalizedScore
        
        // Ceza popup'ı göster
        showBonusPopup(
            message = resourceProvider.getString(R.string.bonus_hint_used),
            points = -1000,
            position = row to col,
            type = BonusType.SPECIAL
        )
        
        // Hint'i uygula
        val newGrid = _uiState.value.currentGrid.map { it.clone() }.toTypedArray()
        newGrid[row][col] = newGrid[row][col].copy(
            value = correctNumber,
            isHint = true,
            isError = false,
            notes = emptySet()
        )
        
        hapticFeedback.hint()
        soundEffects.playHint()
        
        _uiState.update {
            it.copy(
                currentGrid = newGrid,
                hintsUsed = it.hintsUsed + 1,
                selectedCell = hint
            )
        }
        
        checkCompletion()
        autoSave()
    }
    
    fun onUndoPressed() {
        if (moveHistory.isEmpty()) return
        
        val lastMove = moveHistory.removeAt(moveHistory.lastIndex)
        redoStack.add(lastMove)
        
        val newGrid = _uiState.value.currentGrid.map { it.clone() }.toTypedArray()
        newGrid[lastMove.row][lastMove.col] = 
            newGrid[lastMove.row][lastMove.col].copy(
                value = lastMove.oldValue,
                isError = false
            )
        
        _uiState.update {
            it.copy(
                currentGrid = newGrid,
                moves = maxOf(0, it.moves - 1),
                canUndo = moveHistory.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                remainingNumbers = calculateRemainingNumbers(newGrid),
                conflictCells = findConflicts(newGrid)
            )
        }
    }
    
    fun onRedoPressed() {
        if (redoStack.isEmpty()) return
        
        val move = redoStack.removeAt(redoStack.lastIndex)
        moveHistory.add(move)
        
        val newGrid = _uiState.value.currentGrid.map { it.clone() }.toTypedArray()
        newGrid[move.row][move.col] = 
            newGrid[move.row][move.col].copy(value = move.newValue)
        
        _uiState.update {
            it.copy(
                currentGrid = newGrid,
                moves = it.moves + 1,
                canUndo = moveHistory.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                remainingNumbers = calculateRemainingNumbers(newGrid),
                conflictCells = findConflicts(newGrid)
            )
        }
    }
    
    fun toggleNoteMode() {
        _uiState.update { it.copy(isNoteMode = !it.isNoteMode) }
    }
    
    fun onPauseGame() {
        stopTimer()
        _uiState.update { it.copy(isPaused = true) }
        autoSave()
    }
    
    fun onResumeGame() {
        _uiState.update { it.copy(isPaused = false) }
        startTimer()
    }
    
    private fun startTimer() {
        timer?.cancel()
        timer = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
        }
    }
    
    private fun stopTimer() {
        timer?.cancel()
    }
    
    private fun updateNotes(row: Int, col: Int, number: Int) {
        val newGrid = _uiState.value.currentGrid.map { it.clone() }.toTypedArray()
        val currentCell = newGrid[row][col]
        
        val newNotes = if (currentCell.notes.contains(number)) {
            currentCell.notes - number
        } else {
            currentCell.notes + number
        }
        
        newGrid[row][col] = currentCell.copy(notes = newNotes)
        _uiState.update { it.copy(currentGrid = newGrid) }
        
        // Not kullanımını kaydet
        hasUsedNotes = true
    }
    
    private fun checkCompletion() {
        val currentState = _uiState.value.currentGrid.toGridString()
        val solution = _uiState.value.sudoku?.solution ?: return
        
        if (checkCompletionUseCase(currentState, solution)) {
            stopTimer()
            hapticFeedback.success()
            soundEffects.playSuccess()
            
            // ═══════════════════════════════════════════════════════════
            // PUANLAMA SİSTEMİ - FİNAL PUAN HESAPLA
            // ═══════════════════════════════════════════════════════════
            val difficulty = _uiState.value.sudoku?.difficulty ?: "medium"
            val elapsedTimeMs = _uiState.value.elapsedTime
            
            val finalScore = calculateFinalScoreUseCase(
                gameScore = _gameScore.value,
                elapsedTimeMs = elapsedTimeMs,
                difficulty = difficulty,
                usedNotes = hasUsedNotes
            )
            
            _gameScore.value = finalScore
            
            // Özel bonuslar için popup'lar göster
            if (finalScore.perfectGame) {
                showBonusPopup(
                    message = resourceProvider.getString(R.string.bonus_perfect_game),
                    points = 10000,
                    position = null,
                    type = BonusType.PERFECT
                )
            }
            
            if (finalScore.playedWithoutNotes) {
                showBonusPopup(
                    message = resourceProvider.getString(R.string.bonus_no_notes),
                    points = 5000,
                    position = null,
                    type = BonusType.SPECIAL
                )
            }
            
            if (finalScore.speedBonus) {
                showBonusPopup(
                    message = resourceProvider.getString(R.string.bonus_speed),
                    points = 3000,
                    position = null,
                    type = BonusType.TIME
                )
            }
            
            _uiState.update { it.copy(isCompleted = true) }
            saveCompletedGame()
        }
    }
    
    private fun autoSave() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // gameId yoksa kaydetme (henüz yüklenmemiş)
            val uniqueGameId = state.gameId ?: return@launch
            val sudoku = state.sudoku ?: return@launch
            val scoreSnapshot = _gameScore.value
            
            val gameState = GameState(
                gameId = uniqueGameId,  // ← Unique gameId kullan
                userId = currentUserId,
                sudokuId = sudoku.id,
                difficulty = sudoku.difficulty,
                currentState = state.currentGrid.toGridString(),
                notes = state.currentGrid.notesToJson(),
                elapsedTime = state.elapsedTime,
                moves = state.moves,
                hintsUsed = state.hintsUsed,
                isCompleted = state.isCompleted,
                isAbandoned = false,
                lastPlayedAt = System.currentTimeMillis(),
                createdAt = state.createdAt,
                score = scoreSnapshot.finalScore,
                scoreDetails = scoreSnapshot.toJsonString()
            )
            saveGameStateUseCase(gameState)
        }
    }
    
    private fun saveCompletedGame() {
        viewModelScope.launch {
            // UserStats'ı güncelle
            // Achievement kontrolü yap
            // Leaderboard'a ekle
            autoSave()
        }
    }
    
    /**
     * Bonus popup göster (UI için)
     */
    private fun showBonusPopup(
        message: String,
        points: Int,
        position: Pair<Int, Int>?,
        type: BonusType
    ) {
        val event = BonusEvent(
            message = message,
            points = points,
            position = position,
            type = type
        )
        
        _bonusEvents.value = _bonusEvents.value + event
        
        // 2 saniye sonra popup'ı kaldır
        viewModelScope.launch {
            delay(2000)
            _bonusEvents.value = _bonusEvents.value.filter { it != event }
        }
    }
    
    /**
     * Bonus event'i manuel temizle
     */
    fun dismissBonusEvent(event: BonusEvent) {
        _bonusEvents.value = _bonusEvents.value.filter { it != event }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun skipCurrentGame(onSkipped: () -> Unit) {
        viewModelScope.launch {
            // Mevcut oyunu kaydet ve sonra çık
            stopTimer()
            autoSave()
            onSkipped()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopTimer()
        autoSave()
    }
}

data class GameUiState(
    val sudoku: Sudoku? = null,
    val gameId: String? = null,  // ← YENİ: Unique game ID
    val currentGrid: Array<Array<Cell>> = emptyArray(),
    val initialGrid: Array<Array<Cell>> = emptyArray(),
    val selectedCell: Pair<Int, Int>? = null,
    val highlightedNumber: Int? = null,
    val showAffectedAreas: Boolean = false, // Number pad'den mi grid'den mi geldiğini belirler
    val conflictCells: Set<Pair<Int, Int>> = emptySet(), // Çakışan hücreler
    val isNoteMode: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val elapsedTime: Long = 0,
    val moves: Int = 0,
    val hintsUsed: Int = 0,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val remainingNumbers: Map<Int, Int> = (1..9).associateWith { 9 }, // Kaç sayı kaldı
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

data class Move(
    val row: Int,
    val col: Int,
    val oldValue: Int,
    val newValue: Int
)
