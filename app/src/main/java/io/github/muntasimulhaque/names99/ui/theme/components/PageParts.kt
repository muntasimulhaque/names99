package io.github.muntasimulhaque.names99.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.muntasimulhaque.names99.R

/*
 * The furniture every page is built from. Kept in one place so Memorize,
 * Settings and About cannot drift apart: a tracked gold overline, a hairline
 * rule, and a row set the way a table of contents is set.
 */

/** Small caps in gold, widely tracked — the app's only kind of heading label. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier,
    )
}

/**
 * The title of a pushed screen. Same tracked small caps as a section label but
 * in quiet ink, so being one level down reads the same everywhere.
 */
@Composable
fun ScreenLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/**
 * Top bars are paper, exactly like the page beneath them — never a tinted
 * band, and never one that tints itself on scroll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun paperTopBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.background,
    scrolledContainerColor = MaterialTheme.colorScheme.background,
)

/** The way back, identical on every pushed screen. */
@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_back),
        )
    }
}

/** Below this a label would be smaller than it started — a floor, never a target. */
private const val MIN_FIT_SCALE = 0.55f

/**
 * Text set to fit the width it is given, stepping its size down instead of
 * wrapping or ellipsizing.
 *
 * For the handful of places where the words themselves have to survive at any
 * font scale: the app's own name, which must never be cut mid-"Allah", and the
 * bottom bar's labels, which at a system font scale of 2.0 would otherwise read
 * "MEM…" / "SETTI…". Shrinking still leaves them far larger than the default —
 * it only caps growth at what the space can hold. Measured up front, so there
 * is no first-frame flicker the way a layout-feedback loop would have.
 */
@Composable
fun FitText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    BoxWithConstraints(modifier) {
        val available = constraints.maxWidth
        val fitted = remember(text, style, available, measurer) {
            val floor = style.fontSize * MIN_FIT_SCALE
            var candidate = style
            while (candidate.fontSize > floor &&
                measurer.measure(text, candidate, softWrap = false).size.width > available
            ) {
                candidate = candidate.copy(fontSize = candidate.fontSize * 0.95f)
            }
            candidate
        }
        Text(text = text, style = fitted, color = color, maxLines = 1, softWrap = false)
    }
}

/** The thinnest rule the screen can draw — separates matter, never decorates. */
@Composable
fun PageRule(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

/**
 * A table-of-contents row: title (with an optional gloss beneath) and a gold
 * chevron. The whole row is the tap target.
 */
@Composable
fun NavRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = if (subtitle == null) 17.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = title, style = titleStyle, color = titleColor)
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
        )
    }
}
