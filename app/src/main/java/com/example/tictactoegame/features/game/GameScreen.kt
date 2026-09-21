@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tictactoegame.features.game

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tictactoegame.R
import com.example.tictactoegame.core.components.AppButton
import com.example.tictactoegame.core.components.AppDialog
import com.example.tictactoegame.core.components.AppIconButton
import com.example.tictactoegame.core.components.AppLoaderLottie
import com.example.tictactoegame.core.components.AppScaffold
import com.example.tictactoegame.core.components.AppText
import com.example.tictactoegame.core.utils.SideEffects
import com.example.tictactoegame.ui.theme.CustomBlue
import com.example.tictactoegame.ui.theme.CustomRed

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    BackHandler(true) {
        viewModel.onEvent(GameContract.Event.ClickExit)
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

    val turnText = if (state.winner != 0) {
        when (state.winner) {
            1 -> stringResource(R.string.x_wins)
            -1 -> stringResource(R.string.o_wins)
            2 -> stringResource(R.string.draw)
            else -> ""
        }
    } else {
        if (state.switcher) stringResource(R.string.x_s_turn) else stringResource(R.string.o_s_turn)
    }

    val textAlign = if (state.winner != 0) {
        when (state.winner) {
            1 -> Alignment.TopStart
            -1 -> Alignment.TopEnd
            else -> Alignment.TopCenter
        }
    } else {
        if (state.switcher) Alignment.TopStart else Alignment.TopEnd
    }

    // Combine text and alignment into a single state for smooth transitions
    val animationState = turnText to textAlign

    AppScaffold { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E22))
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {

            // Outer modifier is fixed, alignment handled inside animation
            AnimatedContent(
                targetState = animationState,
                label = "Turn Text Animation",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
            ) { (targetText, targetAlign) ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = targetAlign
                ) {
                    AppText(
                        text = targetText,
                        style = MaterialTheme.typography.displayLarge,
                        color = when (targetText) {
                            stringResource(R.string.x_s_turn) -> CustomRed
                            stringResource(R.string.o_s_turn) -> CustomBlue
                            else -> Color.Green
                        },
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(90.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 0..2) {
                    AppRow(
                        text1 = state.board[i * 3].symbol,
                        text2 = state.board[i * 3 + 1].symbol,
                        text3 = state.board[i * 3 + 2].symbol,
                        click1 = { viewModel.onEvent(GameContract.Event.PlayerMove(i * 3)) },
                        click2 = { viewModel.onEvent(GameContract.Event.PlayerMove(i * 3 + 1)) },
                        click3 = { viewModel.onEvent(GameContract.Event.PlayerMove(i * 3 + 2)) },
                        isIndex1Present = state.winningPath.contains(i * 3),
                        isIndex2Present = state.winningPath.contains(i * 3 + 1),
                        isIndex3Present = state.winningPath.contains(i * 3 + 2)
                    )
                }
            }

            if (state.winner == 1 || state.winner == -1) {
                AppLoaderLottie(
                    modifier = Modifier.fillMaxSize(),
                    lottieRes = R.raw.trophy,
                    iterations = 1,
                    contentScale = ContentScale.Fit
                )
                AppLoaderLottie(
                    modifier = Modifier.fillMaxSize(),
                    lottieRes = R.raw.confetti,
                    iterations = 1,
                    contentScale = ContentScale.Crop
                )
            }

            AnimatedVisibility(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp)
                    .align(Alignment.BottomCenter),
                visible = state.restartButtonVisibility,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .size(70.dp),
                    text = stringResource(R.string.restart),
                    onClick = {
                        viewModel.onEvent(GameContract.Event.ResetGame)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.displayMedium
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp)
            ) {
                AppIconButton(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    onClick = { viewModel.onEvent(GameContract.Event.ClickExit) },
                    animateOnClick = true
                )
            }
        }

        when(state.dialogState) {
            is GameContract.DialogState.Exit -> {
                AppDialog(
                    title = "Quit Game?",
                    message = "Are you sure you want to leave this match?\nYour current match will end.",
                    confirmText = "Quit Game",
                    dismissText = "Cancel",
                    icon = Icons.Default.Warning,
                    onConfirm = { viewModel.onEvent(GameContract.Event.ConfirmDialogAction) },
                    onDismiss = { viewModel.onEvent(GameContract.Event.DismissDialog) }
                )
            }
            is GameContract.DialogState.Hidden -> {}
        }
    }
}

@Composable
fun AppRow(
    text1: String,
    text2: String,
    text3: String,
    click1: () -> Unit,
    click2: () -> Unit,
    click3: () -> Unit,
    isIndex1Present: Boolean = false,
    isIndex2Present: Boolean = false,
    isIndex3Present: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 0..2) {
            val isPresent = when (i) {
                0 -> isIndex1Present
                1 -> isIndex2Present
                else -> isIndex3Present
            }

            val buttonText = when (i) {
                0 -> text1
                1 -> text2
                else -> text3
            }

            val clickAction = when (i) {
                0 -> click1
                1 -> click2
                else -> click3
            }

            // Dark gray box color from screenshot
            val defaultBoxColor = Color(0xFF28282D)
            val animatedContainerColor by animateColorAsState(
                targetValue = if (isPresent) Color(0xFF388E3C) else defaultBoxColor,
                animationSpec = tween(durationMillis = 600),
                label = "Container Color Animation"
            )

            // X will be white, O will be blue
            val symbolColor = when (buttonText.uppercase()) {
                "X" -> CustomRed
                "O" -> CustomBlue // Bright blue
                else -> Color.Transparent
            }

            val animatedContentColor by animateColorAsState(
                targetValue = if (isPresent) Color.White else symbolColor,
                animationSpec = tween(durationMillis = 600),
                label = "Content Color Animation"
            )

            AppButton(
                text = buttonText,
                onClick = clickAction,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = animatedContainerColor,
                    contentColor = animatedContentColor
                ),
                shape = RoundedCornerShape(20.dp), // Increased roundness
                textStyle = MaterialTheme.typography.displayLarge
            )
        }
    }
}