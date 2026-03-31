package com.example.tictactoegame.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class TicTacToeViewModel : ViewModel() {
    // 0 = empty, 1 = player (X), -1 = player (O)
    var board = mutableStateListOf(
        0, 0, 0,
        0, 0, 0,
        0, 0, 0
    )
        private set

    // to switch the player
    var switcher by mutableStateOf(false)
        private set

    // to check the winner
    var winner by mutableIntStateOf(0)
        private set

    // for restart button
    var visibility by mutableStateOf(false)
        private set

    // to set green color to winning path
    var winningPath = mutableStateListOf<Int>()

    // all the possible winning combinations
    val winningCombinations = listOf(
        // Rows
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        // Columns
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        // Diagonals
        listOf(0, 4, 8), listOf(2, 4, 6)
    )

    fun switchPlayer() {
        switcher = !switcher
    }

    fun showRestart() {
        visibility = true
    }

    fun hideRestart() {
        visibility = false
    }

    // to make a move
    fun playerMove(btnId: Int) {
        // check if the game is over
        if (board[btnId] != 0 || winner != 0) return

        // do the work
        board[btnId] = if (switcher) 1 else -1

        // check the game status
        checkStatus()

        // switch the player
        if(winner == 0) switchPlayer()
    }

    fun getSymbol(index: Int): String {
        return when (board[index]) {
            1 -> "X"
            -1 -> "O"
            else -> ""
        }
    }

    fun checkStatus() {
        for (combination in winningCombinations) {
            val a = combination[0]
            val b = combination[1]
            val c = combination[2]

            if (board[a] != 0 && board[a] == board[b] && board[a] == board[c]) {
                winner = board[a]
                winningPath = combination.toMutableStateList()
                showRestart()
                return
            }
        }

        if (!board.contains(0) && !visibility) {
            winner = 2
            showRestart()
        }
    }

    fun resetGame() {
        board.fill(0)
        switcher = false
        winner = 0
        winningPath.clear()
        hideRestart()
    }
}