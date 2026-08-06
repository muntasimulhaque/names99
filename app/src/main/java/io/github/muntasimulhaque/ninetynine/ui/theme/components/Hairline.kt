package io.github.muntasimulhaque.ninetynine.ui.theme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.muntasimulhaque.ninetynine.ui.theme.Motion

/**
 * A whisper of a progress bar: a hairline gold fill on a paper track.
 * Used for memorization progress, flashcard decks, and the quiz.
 */
@Composable
fun HairlineProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 3.dp,
) {
    val fraction by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = Motion.tween(Motion.CALM, easing = Motion.Settle),
        label = "hairlineProgress",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            // `outline`, not `outlineVariant`: this track is where the bar
            // ends, so it carries meaning. With an invisible track there is
            // nothing to judge the gold fill against. outlineVariant is 1.42:1
            // on paper; WCAG 1.4.11 asks 3:1 of non-text that informs.
            .background(MaterialTheme.colorScheme.outline),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(CircleShape)
                // Not `secondary`: the gold fill at 1.30:1 (light) / 2.26:1
                // (dark) against the track fails WCAG 1.4.11. The scheme's own
                // onSecondaryContainer is a deep bronze in light (3.38:1 vs
                // outline) and a pale gold in dark (3.42:1) — same family,
                // readable boundary, no new colours.
                .background(MaterialTheme.colorScheme.onSecondaryContainer),
        )
    }
}
