package com.grimoire.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GrimoireGold,
    onPrimary = GrimoireBg,
    primaryContainer = GrimoireCardElevated,
    onPrimaryContainer = GrimoireGoldLight,
    secondary = GrimoirePurple,
    onSecondary = TextPrimary,
    secondaryContainer = GrimoirePurpleDark,
    onSecondaryContainer = GrimoireGoldLight,
    tertiary = GrimoireCyan,
    onTertiary = GrimoireBg,
    background = GrimoireBg,
    onBackground = TextPrimary,
    surface = GrimoireCard,
    onSurface = TextPrimary,
    surfaceVariant = GrimoireCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = GrimoireBorder,
    error = GrimoireRed,
    onError = TextPrimary
)

@Composable
fun GrimoireTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
