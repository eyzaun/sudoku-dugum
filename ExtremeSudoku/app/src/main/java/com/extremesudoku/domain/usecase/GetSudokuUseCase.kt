package com.extremesudoku.domain.usecase

import com.extremesudoku.data.models.Sudoku
import com.extremesudoku.data.repository.SudokuRepository
import javax.inject.Inject

class GetSudokuUseCase @Inject constructor(
    private val repository: SudokuRepository
) {
    suspend operator fun invoke(id: String? = null): Result<Sudoku> {
        return if (id != null) {
            repository.getSudoku(id)
        } else {
            repository.getRandomSudoku()
        }
    }
}
