package io.github.muntasimulhaque.ninetynine.ui.theme

import android.app.Activity
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.muntasimulhaque.ninetynine.data.ThemeMode

/**
 * The reader's text-size preference, for text sized outside the type scale.
 * `ArabicText` takes explicit sp values, so without this the slider would move
 * the Latin and leave the Arabic behind.
 */
val LocalTextScale = staticCompositionLocalOf { 1f }

/**
 * Whether the theme actually renders dark — the reader's choice, not the
 * system's (BLACK on a light phone must still draw light-mode system bars).
 * The fixed hero plates (hero card, quiz card, flashcard front, share card)
 * are the same emerald in every theme, so they take a border in dark modes
 * to keep their edge against the near-black page — see HeroPlateBorder.
 */
val LocalDarkTheme = staticCompositionLocalOf { false }

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

    CompositionLocalProvider(
        LocalTextScale provides textScale,
        LocalDarkTheme provides darkTheme,
    ) {
        // Read the system animator scale so Motion can collapse to snap()
        // when the user has turned animations off.
        val context = LocalContext.current
        val motionScale = remember {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            )
        }
        CompositionLocalProvider(LocalMotionScale provides motionScale) {
            MaterialTheme(
                colorScheme = colors,
                typography = appTypography(textScale),
                shapes = AppShapes,
                content = content,
            )
        }
    }
}
