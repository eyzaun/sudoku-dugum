package com.extremesudoku.domain.usecase

import com.extremesudoku.data.models.GameState
import com.extremesudoku.data.repository.SudokuRepository
import javax.inject.Inject

class GetGameStateUseCase @Inject constructor(
    private val repository: SudokuRepository
) {
    suspend operator fun invoke(gameId: String): GameState? {
        return repository.getGameState(gameId)
    }
}
