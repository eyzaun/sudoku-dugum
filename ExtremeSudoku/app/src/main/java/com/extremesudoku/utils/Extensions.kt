package com.extremesudoku.utils

import com.extremesudoku.data.models.Cell
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Extension functions
fun String.toCellGrid(): Array<Array<Cell>> {
    return Array(9) { row ->
        Array(9) { col ->
            val index = row * 9 + col
            val value = this.getOrNull(index)?.toString()?.toIntOrNull() ?: 0
            Cell(value = value, isInitial = value != 0)
        }
    }
}

fun Array<Array<Cell>>.toGridString(): String {
    return this.joinToString("") { row ->
        row.joinToString("") { it.value.toString() }
    }
}

fun Array<Array<Cell>>.notesToJson(): String {
    val notesMap = mutableMapOf<String, Set<Int>>()
    forEachIndexed { row, cells ->
        cells.forEachIndexed { col, cell ->
            if (cell.notes.isNotEmpty()) {
                notesMap["$row,$col"] = cell.notes
            }
        }
    }
    return Gson().toJson(notesMap)
}

fun String.jsonToNotes(): Map<String, Set<Int>> {
    return try {
        val type = object : TypeToken<Map<String, Set<Int>>>() {}.type
        Gson().fromJson(this, type) ?: emptyMap()
    } catch (e: Exception) {
        emptyMap()
    }
}

fun Array<Array<Cell>>.applyNotesFromJson(notesJson: String): Array<Array<Cell>> {
    val notesMap = notesJson.jsonToNotes()
    val gridCopy = this.map { it.clone() }.toTypedArray()
    
    notesMap.forEach { (key, notes) ->
        val (row, col) = key.split(",").map { it.toInt() }
        if (row in 0..8 && col in 0..8) {
            gridCopy[row][col] = gridCopy[row][col].copy(notes = notes)
        }
    }
    
    return gridCopy
}

fun formatTime(seconds: Long): String {
    if (seconds == Long.MAX_VALUE) return "--:--"
    
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

fun Array<IntArray>.toGridString(): String {
    return this.joinToString("") { row ->
        row.joinToString("")
    }
}
