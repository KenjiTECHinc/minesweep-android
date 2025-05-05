package com.example.minesweep_plus.logic

import android.util.Log
import com.example.minesweep_plus.model.*

object BoardGenerator {

    fun createBoard(rows: Int, cols: Int): List<MutableList<Cell>> {
        return List(rows) {
            MutableList(cols) {
                Cell()
            }
        }
    }

    fun clearBoard(board: MutableList<MutableList<Cell>>) {
        for (row in board) {
            for (cell in row) {
                cell.value = CellValue.Empty
                cell.isOpened = false
                cell.isFlagged = false
            }
        }
    }

    fun fillMines(
        board: MutableList<MutableList<Cell>>,
        rows: Int,
        cols: Int,
        totalMines: Int,
        reservedPositions: List<Pair<Int, Int>>
    ){
        val positions = mutableListOf<Pair<Int, Int>>()

        Log.d("initGame", reservedPositions.toString())

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (!reservedPositions.contains(Pair(row, col))) {
                    positions.add(Pair(row, col))
                }
            }
        }

        positions.shuffle()
        for (i in 0 until totalMines) {
            val (row, col) = positions[i]
            board[row][col].value = CellValue.Mine()
        }
    }

    fun fillNumbers(board: MutableList<MutableList<Cell>>, rows: Int, cols: Int){
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val cell = board[row][col]
                if (cell.value !is CellValue.Mine) {
                    var count = 0
                    for (i in -1..1) {
                        for (j in -1..1) {
                            val newRow = row + i
                            val newCol = col + j
                            if (newRow in 0 until rows && newCol in 0 until cols) {
                                if (board[newRow][newCol].value is CellValue.Mine) {
                                    count++
                                }
                            }
                        }
                    }

                    cell.value = if (count == 0) CellValue.Empty else CellValue.Number(count)
                }
            }
        }
    }

    fun getAdjacentCells(row: Int, col: Int, rows: Int, cols: Int): List<Pair<Int, Int>> {
        val positions = mutableListOf<Pair<Int, Int>>()

        for (i in row - 1..row + 1) {
            for (j in col - 1..col + 1) {
                if (i in 0 until rows && j in 0 until cols) {
                    positions.add(Pair(i, j))
                }
            }
        }

        return positions
    }

    fun clearFlags(board: MutableList<MutableList<Cell>>, rows: Int, cols: Int){
        for (i in 0 until rows){
            for (j in 0 until cols){
                board[i][j].isFlagged = false
            }
        }
    }

    fun initGame(
        board: MutableList<MutableList<Cell>>,
        rows: Int,
        cols: Int,
        totalMines: Int,
        reservedPosition: Pair<Int, Int>
    ){
        clearFlags(board, rows, cols)
        val reservedPositions = getAdjacentCells(reservedPosition.first, reservedPosition.second, rows, cols)
        fillMines(board, rows, cols, totalMines, reservedPositions)
        fillNumbers(board, rows, cols)
    }
}