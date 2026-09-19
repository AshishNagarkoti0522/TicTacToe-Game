@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tictactoegame.features.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tictactoegame.components.AppButton
import com.example.tictactoegame.components.AppDialog
import com.example.tictactoegame.components.AppScaffold
import com.example.tictactoegame.components.AppText
import com.example.tictactoegame.utils.SideEffects

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    navigateToGame: (isSinglePlayer: Boolean) -> Unit = {}
) {
    BackHandler(true) {
        viewModel.onEvent(MainContract.Event.ClickExit)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { sideEffects ->
            when(sideEffects) {
                is SideEffects.NavigateTo -> onBackClick()
                is SideEffects.ShowToast -> Toast.makeText(context, sideEffects.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AppScaffold{ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                contentAlignment = Alignment.Center
            ) {
                AppText(
                    text = "TicTacToe",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .size(70.dp),
                    text = "Play with Friend",
                    onClick = {
                        navigateToGame(false)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium,
                    textStyle = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .size(70.dp),
                    text = "Play with Bot",
                    onClick = {
                        navigateToGame(true)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium,
                    textStyle = MaterialTheme.typography.headlineMedium
                )
            }
        }
        when(state.dialogState) {
            is MainContract.DialogState.Exit -> {
                AppDialog(
                    title = "Exit Game?",
                    message = "Are you sure you want to exit the game?",
                    confirmText = "Exit Game",
                    dismissText = "Cancel",
                    icon = Icons.Default.Warning,
                    onConfirm = { viewModel.onEvent(MainContract.Event.ConfirmDialogAction) },
                    onDismiss = { viewModel.onEvent(MainContract.Event.DismissDialog) }
                )
            }
            is MainContract.DialogState.Hidden -> {}
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}