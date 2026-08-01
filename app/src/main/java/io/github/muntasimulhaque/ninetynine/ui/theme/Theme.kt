package io.github.muntasimulhaque.ninetynine.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.muntasimulhaque.ninetynine.data.ThemeMode

/**
 * The reader's text-size preference, for text sized outside the type scale.
 * `ArabicText` takes explicit sp values, so without this the slider would move
 * the Latin and leave the Arabic behind.
 */
val LocalTextScale = staticCompositionLocalOf { 1f }

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

    // The status-bar icons follow the theme the reader chose, not the one the
    // system is in — otherwise choosing Black on a light phone paints dark
    // icons onto a black bar and the clock disappears.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalTextScale provides textScale) {
        MaterialTheme(
            colorScheme = colors,
            typography = appTypography(textScale),
            shapes = AppShapes,
            content = content,
        )
    }
}
