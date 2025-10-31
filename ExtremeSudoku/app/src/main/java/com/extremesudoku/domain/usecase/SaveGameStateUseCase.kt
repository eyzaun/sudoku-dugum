package com.extremesudoku.domain.usecase

import com.extremesudoku.data.models.GameState
import com.extremesudoku.data.repository.SudokuRepository
import javax.inject.Inject

class SaveGameStateUseCase @Inject constructor(
    private val repository: SudokuRepository
) {
    suspend operator fun invoke(gameState: GameState) {
        repository.saveGameState(gameState)
    }
}
