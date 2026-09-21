package com.example.tictactoegame.core.components

import android.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun AppIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    shape: Shape = CircleShape,
    size: Dp = 48.dp,
    enabled: Boolean = true,
    isFilled: Boolean = false,
    colors: IconButtonColors? = null,
    animateOnClick: Boolean = false // New parameter: default is off
) {
    val tooltipState = rememberTooltipState()

    // Interaction source to track press state
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animation state logic
    val scale by animateFloatAsState(
        targetValue = if (animateOnClick && isPressed) 0.90f else 1f, // 0.90f gives a nice bouncy feel for smaller icons
        label = "iconButtonScaleAnim"
    )

    // Smart default colors
    val actualColors = colors ?: if (isFilled) {
        IconButtonDefaults.filledIconButtonColors()
    } else {
        IconButtonDefaults.iconButtonColors()
    }

    val iconContent: @Composable () -> Unit = {
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(size * 0.6f)
            )
        } else if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }

    // Common modifier for animation
    val animatedModifier = Modifier
        .size(size)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above
        ),
        tooltip = {
            PlainTooltip {
                AppText(text = contentDescription)
            }
        },
        state = tooltipState,
        modifier = modifier
    ) {
        if (isFilled) {
            FilledIconButton(
                onClick = onClick,
                modifier = animatedModifier, // Applied animation modifier
                enabled = enabled,
                shape = shape,
                colors = actualColors,
                interactionSource = interactionSource // Required to track clicks
            ) {
                iconContent()
            }
        } else {
            IconButton(
                onClick = onClick,
                modifier = animatedModifier.clip(shape), // Applied animation modifier + clip
                enabled = enabled,
                colors = actualColors,
                interactionSource = interactionSource // Required to track clicks
            ) {
                iconContent()
            }
        }
    }
}

// Demo/Boilerplate for visualization
@ExperimentalMaterial3Api
@Preview(showBackground = true, name = "App Icon Button Demo")
@Composable
fun AppIconButtonDemo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 1. Standard (No Animation by default)
        AppIconButton(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            onClick = { }
        )

        // 2. Filled with Animation ON
        AppIconButton(
            imageVector = Icons.Default.Star,
            contentDescription = "Favorite",
            isFilled = true,
            animateOnClick = true,
            onClick = { }
        )

        AppIconButton(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            isFilled = true,
            shape = RoundedCornerShape(12.dp),
            onClick = { },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // 3. Normal Square with Painter (No animation)
        AppIconButton(
            painter = painterResource(id = R.drawable.ic_menu_camera),
            contentDescription = "Camera",
            isFilled = false,
            shape = RoundedCornerShape(8.dp),
            onClick = { }
        )
    }
}