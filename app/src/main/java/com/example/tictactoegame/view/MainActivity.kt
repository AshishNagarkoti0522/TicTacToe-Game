package com.example.tictactoegame.view

import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.tictactoegame.R
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RawRes
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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.tictactoegame.ui.theme.TicTacToeGameTheme
import com.example.tictactoegame.viewmodel.TicTacToeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeGameTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {
                        AppTopBar(
                            title = stringResource(R.string.app_name)
                        )
                    }
                ) { innerPadding ->
                    TicTacToeGame(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TicTacToeGame(modifier: Modifier = Modifier, vm: TicTacToeViewModel = viewModel()) {

    val turnText = if (vm.winner != 0) {
        when (vm.winner) {
            1 -> stringResource(R.string.x_wins)
            -1 -> stringResource(
                R.string.o_wins
            )
            2 -> stringResource(R.string.draw)
            else -> ""
        }
    } else {
        if (vm.switcher) stringResource(R.string.x_s_turn) else stringResource(R.string.o_s_turn)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    )  {
        AnimatedContent(
            targetState = turnText,
            label = "Turn Text Animation",
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) { targetText ->
            AppText(
                text = targetText,
                style = MaterialTheme.typography.displaySmall
            )
        }

        Spacer(modifier = Modifier.height(90.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            for (i in 0..2) {
                AppRow(
                    text1 = vm.getSymbol(i * 3),
                    text2 = vm.getSymbol(i * 3 + 1),
                    text3 = vm.getSymbol(i * 3 + 2),
                    click1 = { vm.playerMove(i * 3) },
                    click2 = { vm.playerMove(i * 3 + 1) },
                    click3 = { vm.playerMove(i * 3 + 2) },
                    isIndex1Present = vm.winningPath.contains(i * 3),
                    isIndex2Present = vm.winningPath.contains(i * 3 + 1),
                    isIndex3Present = vm.winningPath.contains(i * 3 + 2)
                )
            }
        }
        if (vm.winner == 1 || vm.winner == -1) {
            AppLoaderLottie(
                modifier = Modifier
                    .padding(top = 10.dp),
                lottieRes = R.raw.trophy
            )
            AppLoaderLottie(
                modifier = Modifier
                    .padding(top = 10.dp),
                lottieRes = R.raw.confetti
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .align(Alignment.BottomCenter),
            visible = vm.visibility,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ){
            AppButton(
                text = stringResource(R.string.restart),
                onClick = {
                    vm.resetGame()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
fun AppLoaderLottie(
    @RawRes lottieRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 350.dp,
    speed: Float = 1f
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1,
        speed = speed
    )

    Box(
        modifier = modifier
            .padding(top = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(size)
        )
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
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
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

            val clickAction = when(i) {
                0 -> click1
                1 -> click2
                else -> click3
            }

            val animatedContainerColor by animateColorAsState(
                targetValue = if(isPresent) Color.Green else MaterialTheme.colorScheme.surface,
                animationSpec = tween(durationMillis = 600),
                label = "Container Color Animation"
            )

            val animatedContentColor by animateColorAsState(
                targetValue = if(isPresent) Color.White else MaterialTheme.colorScheme.primary,
                animationSpec = tween(durationMillis = 600),
                label = "Content Color Animation"
            )

            AppButton(
                text = buttonText,
                onClick = clickAction,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(0.5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = animatedContainerColor,
                    contentColor = animatedContentColor
                )
            )
        }
    }
}

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = fontWeight
    )
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = shape,
        colors = colors,

    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    centerTitle: Boolean = true
) {
    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
    )

    val navigationIcon: @Composable () -> Unit = {
        if (onBackClick != null) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    }

    val titleContent: @Composable () -> Unit = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (centerTitle) {
        CenterAlignedTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = topAppBarColors
        )
    } else {
        TopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = topAppBarColors
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TicTacToeGamePreview() {
    TicTacToeGameTheme {
        TicTacToeGame()
    }
}