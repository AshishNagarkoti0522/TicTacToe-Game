package com.example.tictactoegame.components

// Lottie for Jetpack Compose (Latest version use karna)
// implementation("com.airbnb.android:lottie-compose:6.4.0")

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
// 🔥 Clean Imports for Lottie
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AppLoaderLottie(
    @RawRes lottieRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    speed: Float = 1f,
    iterations: Int = LottieConstants.IterateForever, // 🔥 Optional: Kitni baar chalana hai
    isPlaying: Boolean = true
) {
    // 1. Composition load karna (from res/raw)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))

    // 2. Progress state handle karna (Loop, Speed, Play/Pause)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = speed,
        isPlaying = isPlaying
    )

    // 3. Wrapper UI
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(size) // Size directly control karne ke liye
        )
    }
}

// ==========================================
// 🚀 DEMO: Boilerplate for Visualization
// ==========================================
@Preview(showBackground = true, name = "App Lottie Loader Demo")
@Composable
fun AppLoaderLottieDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        // 1. Full Screen Centered Loader (e.g., Initial Load ke liye)
        AppLoaderLottie(
            // lottieRes = R.raw.loading_animation, // Asli app me yeh line un-comment kar lena
            lottieRes = 0, // 👈 Preview ke liye dummy value
            modifier = Modifier.weight(1f),
            size = 200.dp
        )

        // 2. Small Inline Loader (e.g., Pagination ya Button click ke paas)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // AppText("Fetching Data...") // Apna custom text component laga lena
            AppLoaderLottie(
                // lottieRes = R.raw.loading_animation_small,
                lottieRes = 0,
                size = 24.dp,
                speed = 1.5f // Agar chota loader thoda fast chalana ho
            )
        }
    }
}