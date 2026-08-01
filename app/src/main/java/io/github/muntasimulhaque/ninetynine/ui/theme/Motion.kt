package io.github.muntasimulhaque.ninetynine.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/**
 * One motion vocabulary for the whole app: nothing snaps, nothing bounces
 * hard — everything settles, like a page being laid down.
 */
object Motion {
    /** Small state changes: tint, selection. */
    const val QUICK = 180

    /** Content appearing or turning: cards, pages, reveals. */
    const val GENTLE = 350

    /** Screen-level entrances. */
    const val CALM = 500

    /** Decelerating ease used for entrances. */
    val Settle: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Soft spring for tactile feedback (press scale, pops). */
    fun <T> soft() = spring<T>(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)

    /** A little more life, for confirmation pops. */
    fun <T> lively() = spring<T>(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium)
}
