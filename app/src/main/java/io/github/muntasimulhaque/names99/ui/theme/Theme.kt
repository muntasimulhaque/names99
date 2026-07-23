package io.github.muntasimulhaque.names99.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.muntasimulhaque.names99.data.ThemeMode

@Composable
fun Names99Theme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    textScale: Float = 1f,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.BLACK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colors = when {
        themeMode == ThemeMode.BLACK -> BlackColors
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colors,
        typography = appTypography(textScale),
        shapes = AppShapes,
        content = content,
    )
}
