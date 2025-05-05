package com.example.minesweep_plus.ui

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun LevelSelectorScreen(onStartGame: (rows: Int, cols: Int, mines: Int) -> Unit) {
    var selectedLevel by remember { mutableStateOf("Easy") }
    val levels = listOf("Easy", "Medium", "Custom")

    // Custom config
    var customRows by remember { mutableStateOf("9") }
    var customCols by remember { mutableStateOf("9") }
    var customMines by remember { mutableStateOf("10") }

    var rowError by remember { mutableStateOf(false) }
    var colError by remember { mutableStateOf(false) }
    var mineError by remember { mutableStateOf(false) }

    val minRows = 5
    val maxRows = 11
    val minCols = 5
    val maxCols = 11
    val minMines = 1

// Add helper values to compute max mines
    val rowsInt = customRows.toIntOrNull() ?: 0
    val colsInt = customCols.toIntOrNull() ?: 0
    val maxMines = ((rowsInt - 1) * (colsInt - 1)).coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Difficulty")

        levels.forEach { level ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { selectedLevel = level }
            ) {
                RadioButton(
                    selected = selectedLevel == level,
                    onClick = { selectedLevel = level }
                )
                Text(level)
            }
        }

        if (selectedLevel == "Custom") {
            OutlinedTextField(
                value = customRows,
                onValueChange = {
                    customRows = it
                    val newValue = it.toIntOrNull()
                    rowError = newValue == null || newValue !in minRows..maxRows
                },
                label = { Text("Rows (5–11)") },
                isError = rowError,
                singleLine = true
            )
            if (rowError) {
                Text("Please enter a number between 5 and 11", color = Color.Red)
            }
            OutlinedTextField(
                value = customCols,
                onValueChange = {
                    customCols = it
                    val newValue = it.toIntOrNull()
                    colError = newValue == null || newValue !in minCols..maxCols
                },
                label = { Text("Columns (5–11)") },
                isError = colError,
                singleLine = true
            )
            if (colError) {
                Text("Please enter a number between 5 and 11", color = Color.Red)
            }
            OutlinedTextField(
                value = customMines,
                onValueChange = {
                    customMines = it
                    val newValue = it.toIntOrNull()
                    mineError = newValue == null || newValue < minMines || newValue > maxMines
                },
                label = { Text("Mines (1–${maxMines})") },
                isError = mineError,
                singleLine = true
            )
            if (mineError) {
                Text("Mines must be between 1 and $maxMines", color = Color.Red)
            }
        }

        Button(onClick = {
            val (rows, cols, mines) = when (selectedLevel) {
                "Easy" -> Triple(5, 5, 4)
                "Medium" -> Triple(7, 7, 7)
                else -> Triple(
                    customRows.toIntOrNull() ?: 9,
                    customCols.toIntOrNull() ?: 9,
                    customMines.toIntOrNull() ?: 10
                )
            }
            if (!rowError && !colError && !mineError) {
                onStartGame(rows, cols, mines)
            }
        },
            enabled = !rowError && !colError && !mineError
        ) {
            Text("Start Game")
        }
    }
}