package io.github.muntasimulhaque.ninetynine.ui.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.muntasimulhaque.ninetynine.R
import io.github.muntasimulhaque.ninetynine.ui.theme.Motion
import io.github.muntasimulhaque.ninetynine.ui.theme.rememberHaptics

/**
 * The one tactile ritual of the app: marking a name as learned.
 * A quiet outlined pill that fills, grows a check, and pops softly.
 */
@Composable
fun LearnedButton(
    learned: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberHaptics()
    val colors = MaterialTheme.colorScheme

    val container by animateColorAsState(
        targetValue = if (learned) colors.primaryContainer else colors.surface,
        animationSpec = tween(Motion.QUICK),
        label = "learnedContainer",
    )
    val content by animateColorAsState(
        targetValue = if (learned) colors.onPrimaryContainer else colors.onSurfaceVariant,
        animationSpec = tween(Motion.QUICK),
        label = "learnedContent",
    )
    val border by animateColorAsState(
        targetValue = if (learned) colors.primaryContainer else colors.outline,
        animationSpec = tween(Motion.QUICK),
        label = "learnedBorder",
    )

    // A soft pop each time the state actually changes (not on first show).
    val scale = remember { Animatable(1f) }
    val seeded = remember { mutableStateOf(false) }
    LaunchedEffect(learned) {
        if (!seeded.value) {
            seeded.value = true
        } else {
            scale.snapTo(0.94f)
            scale.animateTo(1f, Motion.lively())
        }
    }

    Surface(
        onClick = {
            haptics.confirm()
            onToggle()
        },
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        },
        shape = CircleShape,
        color = container,
        border = BorderStroke(1.dp, border),
    ) {
        Row(
            modifier = Modifier
                .height(44.dp)
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = learned,
                enter = fadeIn(tween(Motion.QUICK)) + expandHorizontally(tween(Motion.QUICK)),
                exit = fadeOut(tween(Motion.QUICK)) + shrinkHorizontally(tween(Motion.QUICK)),
            ) {
                Row {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = content,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically),
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
            Text(
                text = stringResource(if (learned) R.string.learned else R.string.mark_learned),
                style = MaterialTheme.typography.labelLarge,
                color = content,
            )
        }
    }
}
