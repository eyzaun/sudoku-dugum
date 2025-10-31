package com.extremesudoku.domain.usecase

import javax.inject.Inject

class GetHintUseCase @Inject constructor(
    private val solveSudokuUseCase: SolveSudokuUseCase
) {
    operator fun invoke(currentState: String, solution: String): Pair<Int, Int>? {
        // Boş hücreleri bul
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in currentState.indices) {
            if (currentState[i] == '0' || currentState[i] != solution[i]) {
                val row = i / 9
                val col = i % 9
                emptyCells.add(Pair(row, col))
            }
        }
        
        if (emptyCells.isEmpty()) return null
        
        // Random bir boş hücre seç
        return emptyCells.random()
    }
}
