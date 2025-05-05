package com.example.minesweep_plus.logic

import android.util.Log
import com.example.minesweep_plus.model.Cell
import com.example.minesweep_plus.model.CellValue

object GameLogic {

    fun floodFill(row: Int, col: Int, board: MutableList<MutableList<Cell>>) {
        val directions =
            listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)

        val stack = ArrayDeque<Pair<Int, Int>>()
        stack.add(Pair(row, col))

        while (stack.isNotEmpty()) {
            val (r, c) = stack.removeFirst()
            val cell = board[r][c]
//        if (cell.isOpened || cell.isFlagged) continue
            cell.isOpened = true

            if (cell.value is CellValue.Empty) {
                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in board.indices && nc in board[0].indices) {
                        val neighborCell = board[nr][nc]
                        if (!neighborCell.isOpened && neighborCell.value !is CellValue.Mine && !neighborCell.isFlagged) {
                            stack.add(Pair(nr, nc))
                        }
                    }
                }
            }
        }
        Log.d("myTag", "Reveal adjacent cells!")
    }

    fun revealAllMines(board: MutableList<MutableList<Cell>>) {
        for (row in board) {
            for (cell in row) {
                if (cell.value is CellValue.Mine) {
                    cell.isOpened = true
                }
            }
        }
    }

    fun checkWin(board: List<List<Cell>>): Boolean {
        val flattenedBoard = board.flatten()

        val checkFlags = flattenedBoard.all{ cell ->
            (cell.value is CellValue.Mine && cell.isFlagged) ||
                    (cell.value !is CellValue.Mine && !cell.isFlagged)
        }

        val checkSafeOpens = flattenedBoard.all { cell ->
            (cell.value !is CellValue.Mine && cell.isOpened) ||
                    (cell.value is CellValue.Mine && !cell.isOpened)
        }

        return checkFlags || checkSafeOpens

//        return board.flatten().all {
//            (it.value is CellValue.Mine && !it.isOpened) || (it.value !is CellValue.Mine && it.isOpened)
//        }
    }
}