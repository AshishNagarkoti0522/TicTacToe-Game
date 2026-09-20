package com.example.tictactoegame.features.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tictactoegame.navigation.AppRoutes
import com.example.tictactoegame.core.utils.SideEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(GameContract.State())
    val state = _state.asStateFlow()

    private val args = savedStateHandle.toRoute<AppRoutes.Game>()
    val isSinglePlayer = args.isSinglePlayer

    private val _sideEffects = Channel<SideEffects>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val winningCombinations = listOf(
        // Rows
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        // Columns
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        // Diagonals
        listOf(0, 4, 8), listOf(2, 4, 6)
    )

    init {
        if (isSinglePlayer && !state.value.switcher) {
            botMove()
        }
    }

    fun onEvent(event: GameContract.Event) {
        when (event) {
            is GameContract.Event.PlayerMove -> {
                if (isSinglePlayer && !state.value.switcher) return
                handleMove(event.btnId)
            }
            is GameContract.Event.ResetGame -> resetGame()
            is GameContract.Event.ClickExit -> {
                if (state.value.xMoves.isEmpty() && state.value.oMoves.isEmpty()) {
                    viewModelScope.launch { _sideEffects.send(SideEffects.NavigateTo) }
                } else {
                    _state.update {
                        it.copy(
                            dialogState = GameContract.DialogState.Exit
                        )
                    }
                }
            }
            is GameContract.Event.DismissDialog -> {
                _state.update {
                    it.copy(
                        dialogState = GameContract.DialogState.Hidden
                    )
                }
            }
            is GameContract.Event.ConfirmDialogAction -> {
                when(state.value.dialogState){
                    is GameContract.DialogState.Exit -> {
                        viewModelScope.launch { _sideEffects.send(SideEffects.NavigateTo) }
                    }
                    is GameContract.DialogState.Hidden -> {

                    }
                }
            }
        }
    }

    private fun handleMove(btnId: Int) {
        val currentState = _state.value

        // check if the cell is already filled or the game is over
        if (currentState.board[btnId] != GameContract.CellValue.EMPTY || currentState.winner != 0) return

        // Update the specific index in the board
        val newBoard = currentState.board.toMutableList()
        val newXMoves = currentState.xMoves.toMutableList()
        val newOMoves = currentState.oMoves.toMutableList()

        if (currentState.switcher) {
            // X's turn
            newBoard[btnId] = GameContract.CellValue.X
            newXMoves.add(btnId)

            if (newXMoves.size > 3) {
                val oldestMove = newXMoves.removeAt(0)
                newBoard[oldestMove] = GameContract.CellValue.EMPTY
            }
        } else {
            // O's turn
            newBoard[btnId] = GameContract.CellValue.O
            newOMoves.add(btnId)

            if (newOMoves.size > 3) {
                val oldestMove = newOMoves.removeAt(0)
                newBoard[oldestMove] = GameContract.CellValue.EMPTY
            }
        }

        // Apply the new board and updated move lists to the state
        _state.update {
            it.copy(
                board = newBoard,
                xMoves = newXMoves,
                oMoves = newOMoves
            )
        }

        // Check the game status after the move
        checkStatus()

        // Switch the player if the game is still ongoing
        if (_state.value.winner == 0) {
            _state.update { it.copy(switcher = !it.switcher) }

            // If it is single-player mode, and now it's the Bot's turn (O / false), trigger bot
            if (isSinglePlayer && !_state.value.switcher) {
                botMove()
            }
        }
    }

    private fun botMove() {
        viewModelScope.launch {
            delay((Random.nextInt(1, 4) * 400L).milliseconds)

            val currentState = _state.value
            if (currentState.winner != 0) return@launch

            // Filter out all currently empty indices
            val emptySpots = currentState.board.mapIndexedNotNull { index, cellValue ->
                if (cellValue == GameContract.CellValue.EMPTY) index else null
            }

            // Pick a random available cell and make a move
            if (emptySpots.isNotEmpty()) {
                val botChoice = emptySpots.random()
                handleMove(botChoice)
            }
        }
    }

    private fun checkStatus() {
        val currentState = _state.value
        val board = currentState.board

        for (combination in winningCombinations) {
            val a = combination[0]
            val b = combination[1]
            val c = combination[2]

            // Check if someone won
            if (board[a] != GameContract.CellValue.EMPTY && board[a] == board[b] && board[a] == board[c]) {
                val winnerInt = if (board[a] == GameContract.CellValue.X) 1 else -1
                _state.update {
                    it.copy(
                        winner = winnerInt,
                        winningPath = combination,
                        restartButtonVisibility = true
                    )
                }
                return
            }
        }

        // Check for draw
        if (!board.contains(GameContract.CellValue.EMPTY) && !currentState.restartButtonVisibility) {
            _state.update {
                it.copy(
                    winner = 2,
                    restartButtonVisibility = true
                )
            }
        }
    }

    private fun resetGame() {
        _state.update { GameContract.State() }

        if (isSinglePlayer && !_state.value.switcher) {
            botMove()
        }
    }
}