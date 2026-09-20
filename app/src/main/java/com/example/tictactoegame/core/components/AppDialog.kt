package com.example.tictactoegame.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

/**
 * A reusable and customizable dialog component that can be used throughout the app.
 *
 * @param title The main title displayed in the dialog.
 * @param message The descriptive message shown below the title.
 * @param confirmText The text displayed on the confirm button. Defaults to "OK".
 * @param dismissText The text displayed on the dismiss button. If null, the dismiss button is hidden.
 * @param icon An optional icon displayed at the top of the dialog.
 * @param onConfirm Invoked when the user taps the confirm button.
 * @param onDismiss Invoked when the user taps the dismiss button or dismisses the dialog.
 */
@Composable
fun AppDialog(
    title: String,
    message: String,
    confirmText: String = "OK",
    dismissText: String? = "Cancel",
    icon: ImageVector? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            { Icon(imageVector = it, contentDescription = "Dialog Icon") }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        // Renders the OutlinedButton only when dismissText is not null.
        dismissButton = dismissText?.let { text ->
            {
                OutlinedButton(onClick = onDismiss) {
                    Text(text)
                }
            }
        }
    )
}

// DEMO & USE CASES
@Preview(showBackground = true, name = "1. Full Dialog (With Icon & 2 Buttons)")
@Composable
fun AppDialogPreview_Full() {
    MaterialTheme {
        AppDialog(
            title = "Delete Account?",
            message = "Are you sure you want to delete your account? This action cannot be undone and all your data will be lost.",
            confirmText = "Delete",
            dismissText = "Cancel",
            icon = Icons.Default.Warning,
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Info Dialog (Only 1 Button)")
@Composable
fun AppDialogPreview_InfoOnly() {
    MaterialTheme {
        AppDialog(
            title = "Update Successful",
            message = "Your profile has been updated successfully.",
            confirmText = "Got it",
            dismissText = null, // Hides the cancel button.
            icon = null,        // Hides the icon.
            onConfirm = {},
            onDismiss = {}
        )
    }
}