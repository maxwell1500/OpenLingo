package com.duo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Duolingo vibrant brand colors
val DuoGreen = Color(0xFF58CC02)
val DuoBlue = Color(0xFF1CB0F6)
val DuoYellow = Color(0xFFFFC800)
val DuoRed = Color(0xFFFF4B4B)
val DuoOrange = Color(0xFFFF9600)

private val LightColorScheme = lightColorScheme(
    primary = DuoGreen,
    onPrimary = Color.White,
    secondary = DuoBlue,
    onSecondary = Color.White,
    tertiary = DuoYellow,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color(0xFFF7F7F7),
    onSurface = Color(0xFF4B4B4B),
    onBackground = Color(0xFF4B4B4B),
)

private val DarkColorScheme = darkColorScheme(
    primary = DuoGreen,
    onPrimary = Color.White,
    secondary = DuoBlue,
    onSecondary = Color.White,
    tertiary = DuoYellow,
    background = Color(0xFF131F24),
    surface = Color(0xFF1A2A30),
    surfaceVariant = Color(0xFF243640),
    onSurface = Color(0xFFE5E5E5),
    onBackground = Color(0xFFE5E5E5),
)

@Composable
fun DuoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
