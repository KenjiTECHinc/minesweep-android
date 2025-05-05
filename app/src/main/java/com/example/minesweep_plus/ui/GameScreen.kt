package com.example.minesweep_plus.ui

import android.util.Log
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.minesweep_plus.logic.BoardGenerator
import com.example.minesweep_plus.model.Cell
import com.example.minesweep_plus.model.CellValue
import com.example.minesweep_plus.model.GameState
import com.example.minesweep_plus.logic.GameLogic

@Composable
fun GameScreen(modifier: Modifier = Modifier,
               defaultRows: Int = 9,
               defaultCols: Int = 9,
               defaultMines: Int = 10,
               onBackToMenu: (() -> Unit)? = null,
               debugMode: Boolean
) {
    val rows = defaultRows
    val cols = defaultCols
    val totalMines = defaultMines

    var gameState by remember { mutableStateOf(GameState.IDLE) }
    var isFirstClick by remember { mutableStateOf(true) }
    var isFlagMode by remember {mutableStateOf(false)}

    var elapsedSeconds by remember { mutableIntStateOf(0) } //timer

    // Create a game board on first composition
    val board = remember {
        mutableStateListOf<MutableList<Cell>>().apply {
            addAll(BoardGenerator.createBoard(rows, cols))
        }
    }

    val flagsPlaced = remember(board) {
        derivedStateOf {
            board.sumOf { row -> row.count { it.isFlagged } }
        }
    }

    // Start a timer coroutine when the game is ongoing
    LaunchedEffect(gameState) {
        if (gameState == GameState.ONGOING) {
            elapsedSeconds = 0
            while (gameState == GameState.ONGOING) {
                delay(1000L)
                elapsedSeconds++
            }
        }
    }

    fun onRestartGame(){
        gameState = GameState.IDLE
        isFirstClick = true
        elapsedSeconds = 0
        BoardGenerator.clearBoard(board)
    }

    fun onCellClick(row: Int, col: Int, board: MutableList<MutableList<Cell>>) {
        val cell = board[row][col]
        if (cell.isOpened || (cell.isFlagged && !isFlagMode) || gameState == GameState.WON || gameState == GameState.LOST) return

        if (isFlagMode){
            if(!cell.isFlagged && flagsPlaced.value >= totalMines) return
            else {
                cell.isFlagged = !cell.isFlagged
                Log.d("Logic Tag", GameLogic.checkWin(board).toString())
                if (GameLogic.checkWin(board)) {
                    gameState = GameState.WON
                }
                return
            }
        }

        if (isFirstClick) {
            // Ensure first click is never a mine
            BoardGenerator.initGame(
                board = board,
                rows = rows,
                cols = cols,
                totalMines = totalMines,
                reservedPosition = Pair(row, col)
            )
            isFirstClick = false
            if (gameState == GameState.IDLE) gameState = GameState.ONGOING
        }

        cell.isOpened = true
        Log.d("gameScreen", "cell is opened!")

        if (cell.value is CellValue.Mine) {
            gameState = GameState.LOST
            GameLogic.revealAllMines(board)
        } else {
            if (cell.value is CellValue.Empty) {
                // Recursive open
                GameLogic.floodFill(row, col, board)
                Log.d("gameScreen", "reveal Empty!")
            }

            if (GameLogic.checkWin(board)) {
                gameState = GameState.WON
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Minesweeper", modifier = Modifier.padding(bottom = 16.dp))
        Button(
            onClick = { isFlagMode = !isFlagMode },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(if (isFlagMode) "Flag Mode: ON" else "Flag Mode: OFF")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("🚩: ${totalMines - flagsPlaced.value}")
            Spacer(modifier = Modifier.width(32.dp))
            Text("⏱️: $elapsedSeconds s")
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Display the board
        for (i in 0 until rows) {
            Row {
                for (j in 0 until cols) {
                    val cell = board[i][j]
                    val backgroundColor = when {
                        cell.isOpened -> Color.LightGray
                        else -> Color.Gray             // Unopened cell
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(backgroundColor)
                            .padding(2.dp)
                            .border(1.dp,Color.Red)
                            .clickable (
                                onClick = { onCellClick(i, j, board)}
                            )
                    ) {
//                        val cell = board[i][j]
                        if (cell.isOpened || (debugMode && !cell.isFlagged)) {
                            when (val value = cell.value) {
                                is CellValue.Mine -> Text("💣")
                                is CellValue.Number -> Text("${value.count}")
                                is CellValue.Empty -> Text("")
                            }
                        } else if (cell.isFlagged) {
                            Text("🚩")
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { onRestartGame() }
            ) {
                Text("New Game")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { onBackToMenu?.invoke() }) {
                Text("Back to Menu")
            }
        }
        if(gameState == GameState.WON){
            Text("Game Won🎉!")
        } else if (gameState == GameState.LOST){
            Text("Game Over💥!")
        }
    }
}