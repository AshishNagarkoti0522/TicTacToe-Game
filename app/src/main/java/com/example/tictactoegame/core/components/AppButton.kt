package com.example.tictactoegame.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(100.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "buttonScaleAnim"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = 50.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        enabled = enabled,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource
    ) {
        // Icon and Text layout
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
        }

        AppText(
            text = text,
            style = textStyle,
            fontWeight = FontWeight.Bold
        )
    }
}

// Demo for visualization
@Preview(showBackground = true, name = "App Button Demo")
@Composable
fun AppButtonDemo() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Default Usage (Full Width)
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Press Me for Magic",
            onClick = { /* Do something */ }
        )

        // 2. Wrap Content Usage (Overriding the default fillMaxWidth)
        AppButton(
            text = "Small Button",
            onClick = { },
            modifier = Modifier
        )

        // 3. Disabled State
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Disabled Button",
            onClick = { },
            enabled = false
        )
    }
}