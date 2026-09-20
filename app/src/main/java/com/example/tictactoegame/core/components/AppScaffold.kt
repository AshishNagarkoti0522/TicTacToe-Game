package com.example.tictactoegame.core.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = {},
    disableTopBarScroll: Boolean = false,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable (PaddingValues) -> Unit
) {
    // Top Bar Scroll Engine
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Bottom Bar Scroll Engine
    var isScrollingDown by remember { mutableStateOf(false) }
    var bottomBarHeightPx by remember { mutableFloatStateOf(0f) }

    // Detects the scroll direction (up or down).
    val bottomBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -5f) {
                    isScrollingDown = true
                } else if (available.y > 5f) {
                    isScrollingDown = false
                }
                return Offset.Zero
            }
        }
    }

    val bottomBarOffsetPx by animateFloatAsState(
        targetValue = if (isScrollingDown) bottomBarHeightPx else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing),
        label = "BottomBarOffset"
    )

    // Apply nested scrolling only with a TopBar; otherwise, it will consume scroll events.
    var scaffoldModifier = modifier.fillMaxSize()
    if (!disableTopBarScroll) {
        scaffoldModifier =
            scaffoldModifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    }
    scaffoldModifier = scaffoldModifier.nestedScroll(bottomBarScrollConnection)

    Scaffold(
        modifier = scaffoldModifier,
        topBar = { topBar(topAppBarScrollBehavior) },
        bottomBar = {
            Box(
                modifier = Modifier
                    // Calculate the actual bar height, including padding.
                    .onGloballyPositioned { coordinates ->
                        bottomBarHeightPx = coordinates.size.height.toFloat()
                    }
                    // Apply the animated offset.
                    .offset {
                        IntOffset(x = 0, y = bottomBarOffsetPx.roundToInt())
                    }
            ) {
                bottomBar()
            }
        },
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        containerColor = containerColor
    ) { innerPadding ->
        content(innerPadding)
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, name = "Full Integrated App Demo")
@Composable
fun FullAppScaffoldDemo() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = "GitScout Home",
                scrollBehavior = scrollBehavior,
                centerTitle = true
            )
        },
        bottomBar = {

        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Action Clicked!")
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {}
}