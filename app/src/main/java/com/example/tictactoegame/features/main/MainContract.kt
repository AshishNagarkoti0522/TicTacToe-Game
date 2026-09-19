package com.example.tictactoegame.features.main

class MainContract {
    data class State(
        val dialogState: DialogState = DialogState.Hidden
    )

    sealed interface Event {
        data object ClickExit : Event
        data object ConfirmDialogAction : Event
        data object DismissDialog : Event
    }

    sealed interface DialogState {
        data object Hidden : DialogState
        data object Exit : DialogState
    }
}