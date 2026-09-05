package com.example.safesos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SafeSosColorScheme = lightColorScheme(
    primary = SosRed,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = SosGreen,
    background = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color.White,
    error = SosRedDark
)

@Composable
fun SafeSOSTheme(
    statusBarColor: androidx.compose.ui.graphics.Color = SosRed,
    darkIcons: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = statusBarColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
        }
    }
    MaterialTheme(
        colorScheme = SafeSosColorScheme,
        typography = SafeSosTypography,
        content = content
    )
}
