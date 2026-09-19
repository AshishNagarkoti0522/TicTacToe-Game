package com.example.tictactoegame.utils

sealed interface SideEffects {
    data class ShowToast(val message : String) : SideEffects
    data object NavigateTo : SideEffects
}