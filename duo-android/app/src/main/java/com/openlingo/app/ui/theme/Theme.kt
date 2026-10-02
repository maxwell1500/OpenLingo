package com.openlingo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OpenLingoGreen = Color(0xFF2E9E6B)
val OpenLingoBlue = Color(0xFF3D7EA6)
val OpenLingoYellow = Color(0xFFE8A93C)
val OpenLingoRed = Color(0xFFC0392B)
val OpenLingoOrange = Color(0xFFE67E22)

enum class ThemeAccent(
    val label: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val swatchColor: Color,
) {
    TEAL("Onsen Teal (🦫)", Color(0xFF0D9488), Color(0xFF14B8A6), Color(0xFF0D9488)),
    MATCHA("Matcha Green", Color(0xFF2E9E6B), Color(0xFF3D7EA6), Color(0xFF2E9E6B)),
    SAKURA("Sakura Pink", Color(0xFFEC4899), Color(0xFFF472B6), Color(0xFFEC4899)),
    YUZU("Yuzu Citrus", Color(0xFFF59E0B), Color(0xFFEAB308), Color(0xFFF59E0B));

    companion object {
        fun fromName(name: String?): ThemeAccent {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: TEAL
        }
    }
}
enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromName(name: String?): ThemeMode {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: SYSTEM
        }
    }
}

private val LightColorScheme = lightColorScheme(
    primary = OpenLingoGreen,
    onPrimary = Color.White,
    secondary = OpenLingoBlue,
    onSecondary = Color.White,
    tertiary = OpenLingoYellow,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color(0xFFF7F7F7),
    onSurface = Color(0xFF4B4B4B),
    onBackground = Color(0xFF4B4B4B),
)

private val DarkColorScheme = darkColorScheme(
    primary = OpenLingoGreen,
    onPrimary = Color.White,
    secondary = OpenLingoBlue,
    onSecondary = Color.White,
    tertiary = OpenLingoYellow,
    background = Color(0xFF131F24),
    surface = Color(0xFF1A2A30),
    surfaceVariant = Color(0xFF243640),
    onSurface = Color(0xFFE5E5E5),
    onBackground = Color(0xFFE5E5E5),
)

@Composable
fun OpenLingoTheme(
    accent: ThemeAccent = ThemeAccent.TEAL,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val light = lightColorScheme(
        primary = accent.primaryColor,
        onPrimary = Color.White,
        secondary = accent.secondaryColor,
        onSecondary = Color.White,
        tertiary = OpenLingoYellow,
        background = Color.White,
        surface = Color.White,
        surfaceVariant = Color(0xFFF7F7F7),
        onSurfaceVariant = Color(0xFF777777),
        onSurface = Color(0xFF4B4B4B),
        onBackground = Color(0xFF4B4B4B),
    )
    val dark = darkColorScheme(
        primary = accent.primaryColor,
        onPrimary = Color.White,
        secondary = accent.secondaryColor,
        onSecondary = Color.White,
        tertiary = OpenLingoYellow,
        background = Color(0xFF131F24),
        surface = Color(0xFF1A2A30),
        surfaceVariant = Color(0xFF243640),
        onSurfaceVariant = Color(0xFFAAB4C0),
        onSurface = Color(0xFFE5E5E5),
        onBackground = Color(0xFFE5E5E5),
    )
    MaterialTheme(
        colorScheme = if (darkTheme) dark else light,
        content = content,
    )
}
