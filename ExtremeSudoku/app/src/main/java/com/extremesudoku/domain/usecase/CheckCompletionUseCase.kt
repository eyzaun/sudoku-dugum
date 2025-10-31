package com.extremesudoku.domain.usecase

import javax.inject.Inject

class CheckCompletionUseCase @Inject constructor() {
    operator fun invoke(currentState: String, solution: String): Boolean {
        // Tüm hücreler dolu mu ve çözümle eşleşiyor mu kontrol et
        return currentState.length == 81 && 
               solution.length == 81 &&
               !currentState.contains('0') &&
               currentState == solution
    }
}
