package com.example.minesweep_plus

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.minesweep_plus.ui.theme.Minesweep_plusTheme
import com.example.minesweep_plus.ui.GameScreen
import com.example.minesweep_plus.ui.LevelSelectorScreen


class MainActivity : ComponentActivity() {
     @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Minesweep_plusTheme {
                var selectedLevel by remember {
                    mutableStateOf<Triple<Int, Int, Int>?>(null)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    if (selectedLevel == null) {
                        // Show level selector
                        LevelSelectorScreen { rows, cols, mines ->
                            selectedLevel = Triple(rows, cols, mines)
                        }
                    } else {
                        val (rows, cols, mines) = selectedLevel!!
                        GameScreen(
                            modifier = Modifier,
                            defaultRows = rows,
                            defaultCols = cols,
                            defaultMines = mines,
                            onBackToMenu = { selectedLevel = null },
                            debugMode = true
                            )
                    }
                }
            }
        }
    }
}