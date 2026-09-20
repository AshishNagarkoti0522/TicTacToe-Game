@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tictactoegame.features.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tictactoegame.R
import com.example.tictactoegame.core.components.AppButton
import com.example.tictactoegame.core.components.AppDialog
import com.example.tictactoegame.core.components.AppScaffold
import com.example.tictactoegame.core.components.AppText
import com.example.tictactoegame.core.utils.SideEffects
import com.example.tictactoegame.ui.theme.CustomBlue
import com.example.tictactoegame.ui.theme.CustomRed

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
                .paint(
                    painter = painterResource(R.drawable.app_bg_variant),
                    contentScale = ContentScale.Crop
                ),
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
                    modifier = Modifier
                        .background(Color(0xFF111510))
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "TicTacToe",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.W900,
                    color = Color.White
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AppButton(
                    modifier = Modifier
                        .height(70.dp),
                    text = "Play with Friend",
                    shape = MaterialTheme.shapes.extraLarge,
                    onClick = {
                        navigateToGame(false)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomBlue,
                        contentColor = Color.White
                    ),
                    textStyle = MaterialTheme.typography.titleLarge,
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            painter = painterResource(R.drawable.buddy),
                            contentDescription = "Bot Icon",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    modifier = Modifier
                        .height(70.dp),
                    text = "Play with Bot",
                    onClick = {
                        navigateToGame(true)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomRed,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    textStyle = MaterialTheme.typography.titleLarge,
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            painter = painterResource(R.drawable.bot),
                            contentDescription = "Bot Icon",
                            modifier = Modifier.size(64.dp)
                        )
                    }
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