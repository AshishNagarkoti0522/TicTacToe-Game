package com.example.tictactoegame.features.game

class GameContract {
    data class State(
        val board: List<CellValue> = List(9) { CellValue.EMPTY },
        val switcher: Boolean = false, // false = O's turn (based on original logic), true = X's turn
        val winner: Int = 0, // 0 = playing, 1 = X won, -1 = O won, 2 = draw
        val restartButtonVisibility: Boolean = false,
        val winningPath: List<Int> = emptyList(),
        val dialogState: DialogState = DialogState.Hidden,
        val xMoves: List<Int> = emptyList(),
        val oMoves: List<Int> = emptyList()
    )

    sealed interface Event {
        data class PlayerMove(val btnId: Int) : Event
        data object ResetGame : Event
        data object ClickExit : Event
        data object ConfirmDialogAction : Event
        data object DismissDialog : Event
    }

    sealed interface DialogState {
        data object Hidden : DialogState
        data object Exit : DialogState
    }

    enum class CellValue(val symbol: String) {
        X("X"),
        O("O"),
        EMPTY("")
    }
}