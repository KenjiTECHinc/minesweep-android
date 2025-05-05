package com.example.minesweep_plus.model
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Cell {
    var value by mutableStateOf<CellValue>(CellValue.Empty)
    var isOpened by mutableStateOf(false)
    var isFlagged by mutableStateOf(false)
    //var highlight: Highlight? = null
}

sealed class CellValue {
    data class Mine(val symbol: String = "mine") : CellValue()
    data class Number(val count: Int) : CellValue()
    object Empty : CellValue()
}

enum class Highlight {
    Red, Green
}