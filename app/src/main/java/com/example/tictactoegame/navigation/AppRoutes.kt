package com.example.tictactoegame.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    @Serializable
    data object Main : AppRoutes

    @Serializable
    data class Game(val isSinglePlayer: Boolean) : AppRoutes
}