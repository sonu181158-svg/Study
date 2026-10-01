package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ScholarQuestDarkColorScheme = darkColorScheme(
    primary = PrimaryNeonIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryLightIndigo,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = MidnightNavy,
    secondaryContainer = CardNavy,
    onSecondaryContainer = NeonCyanBright,
    tertiary = TrophyGold,
    onTertiary = Color.Black,
    background = MidnightNavy,
    onBackground = TextWhite,
    surface = CardNavy,
    onSurface = TextWhite,
    surfaceVariant = BorderSlate,
    onSurfaceVariant = TextMuted,
    error = DangerRed,
    onError = Color.White
)

private val ScholarQuestLightColorScheme = lightColorScheme(
    primary = PrimaryNeonIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = PrimaryNeonIndigo,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = TrophyGold,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    error = DangerRed,
    onError = Color.White
)

@Composable
fun ScholarQuestTheme(
    darkTheme: Boolean = true, // Default to dark immersive game theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ScholarQuestDarkColorScheme else ScholarQuestLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
