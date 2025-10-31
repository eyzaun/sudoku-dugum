package com.extremesudoku.data.models

data class Cell(
    val value: Int = 0,
    val isInitial: Boolean = false,
    val isError: Boolean = false,
    val isHint: Boolean = false,
    val notes: Set<Int> = emptySet()
) {
    /**
     * Hücre sabit mi? (başlangıç değeri)
     */
    val isFixed: Boolean
        get() = isInitial
    
    /**
     * Hücre boş mu?
     */
    val isEmpty: Boolean
        get() = value == 0
}
