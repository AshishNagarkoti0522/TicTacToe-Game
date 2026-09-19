package com.example.tictactoegame.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tictactoegame.features.game.GameScreen
import com.example.tictactoegame.features.game.GameViewModel
import com.example.tictactoegame.features.main.MainScreen
import com.example.tictactoegame.features.main.MainViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Main,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<AppRoutes.Main> {
            val vm: MainViewModel = hiltViewModel()
            MainScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStackSafe() },
                navigateToGame = { isSinglePlayer ->
                    navController.navigateSafe(AppRoutes.Game(isSinglePlayer))
                }
            )
        }

        composable<AppRoutes.Game> {
            val vm: GameViewModel = hiltViewModel()
            GameScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStackSafe() }
            )
        }
    }
}

fun NavHostController.navigateSafe(
    route: Any,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route) {
            launchSingleTop = true
            builder()
        }
    }
}

fun NavHostController.popBackStackSafe() {
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}