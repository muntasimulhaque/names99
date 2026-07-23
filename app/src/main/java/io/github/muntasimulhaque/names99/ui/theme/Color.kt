package io.github.muntasimulhaque.names99.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(0xFF17624E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDAE9E0),
    onPrimaryContainer = Color(0xFF0E2E24),
    secondary = Color(0xFFA07C24),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF3E7C3),
    onSecondaryContainer = Color(0xFF3D2F05),
    background = Color(0xFFFAF5EA),
    onBackground = Color(0xFF211C12),
    surface = Color(0xFFFFFCF4),
    onSurface = Color(0xFF211C12),
    surfaceVariant = Color(0xFFEFE7D6),
    onSurfaceVariant = Color(0xFF6B6353),
    outline = Color(0xFFB5AB97),
    outlineVariant = Color(0xFFD8CFBB),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFDF8EE),
    surfaceContainer = Color(0xFFF6F0E2),
    surfaceContainerHigh = Color(0xFFF0EADA),
    surfaceContainerHighest = Color(0xFFEAE3D2),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF93CBB5),
    onPrimary = Color(0xFF0E3529),
    primaryContainer = Color(0xFF1D4A3C),
    onPrimaryContainer = Color(0xFFD4EAE0),
    secondary = Color(0xFFD8BC6A),
    onSecondary = Color(0xFF3A2E07),
    secondaryContainer = Color(0xFF54431B),
    onSecondaryContainer = Color(0xFFF4E8C4),
    background = Color(0xFF14120D),
    onBackground = Color(0xFFEAE2D1),
    surface = Color(0xFF1B1913),
    onSurface = Color(0xFFEAE2D1),
    surfaceVariant = Color(0xFF2B2820),
    onSurfaceVariant = Color(0xFFB7AE9C),
    outline = Color(0xFF837B69),
    outlineVariant = Color(0xFF3D392F),
    surfaceContainerLowest = Color(0xFF0E0D0A),
    surfaceContainerLow = Color(0xFF1B1913),
    surfaceContainer = Color(0xFF1F1D17),
    surfaceContainerHigh = Color(0xFF2A2721),
    surfaceContainerHighest = Color(0xFF353127),
)

/** AMOLED variant: true-black background, near-black surfaces. */
val BlackColors = DarkColors.copy(
    background = Color(0xFF000000),
    surface = Color(0xFF0D0C0A),
    surfaceVariant = Color(0xFF171613),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0B0A08),
    surfaceContainer = Color(0xFF100F0C),
    surfaceContainerHigh = Color(0xFF1B1916),
    surfaceContainerHighest = Color(0xFF26231E),
)

/**
 * Fixed deep-emerald tones for the daily-name hero card and the share card,
 * identical in light and dark themes (matches the home-screen widget).
 */
val HeroContainer = Color(0xFF1F4E42)
val HeroGold = Color(0xFFD4B45A)
val HeroText = Color(0xFFF2EDE2)
val HeroSubtext = Color(0xFFBFD5CB)
