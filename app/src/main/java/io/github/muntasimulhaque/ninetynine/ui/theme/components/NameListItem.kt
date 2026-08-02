package io.github.muntasimulhaque.ninetynine.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.muntasimulhaque.ninetynine.R
import io.github.muntasimulhaque.ninetynine.data.Name

/**
 * The list's inset from both page edges. The folio numbers are the leftmost
 * ink on the home screen, so this is the left margin the reader actually sees —
 * it is the same on the right, where the Arabic ends.
 */
val NameRowInset = 20.dp

/** Air between the folio number and the name it belongs to. */
private val FolioGap = 16.dp

/**
 * The folio column is exactly as wide as the widest number in the list, so a
 * two-digit number sits flush against [NameRowInset] with nothing to spare.
 *
 * Measured, not fixed: the numbers are set in `labelLarge`, which grows with
 * the text-size preference and the system font scale, and a fixed column would
 * either leave a widening gap at the page edge or break "99" onto two lines.
 */
@Composable
private fun folioWidth(): Dp {
    val style = MaterialTheme.typography.labelLarge
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(style, measurer) {
        with(density) { measurer.measure("99", style).size.width.toDp() }
    }
}

/**
 * Where the names begin — the indent a divider needs to meet them rather than
 * cutting under the folio numbers. Derived from the same parts as the row, so
 * the two cannot drift.
 */
@Composable
fun nameRowTextInset(): Dp = NameRowInset + folioWidth() + FolioGap

/**
 * One row in the names list: folio number, transliteration + title, learned
 * tick, Arabic.
 *
 * Deliberately says nothing about bookmarks. A row carried a gold margin rule
 * for one version and it was redundant twice over — the name's own page shows a
 * filled bookmark, and the Bookmarks tab is the list of them. A third indicator
 * only added ink to the surface the app opens on.
 */
@Composable
fun NameListItem(
    name: Name,
    learned: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NameRowInset, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // A quiet folio number instead of a badge — typography, not chrome.
            // Right-aligned so the units digits line up down the page; 1-9 sit
            // one digit further in, as they would in a book's index.
            Text(
                text = name.number.toString(),
                modifier = Modifier.width(folioWidth()),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
            )
            Spacer(Modifier.width(FolioGap))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = name.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (learned) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    // Named, so a screen reader announces the state the gold
                    // tick carries visually.
                    contentDescription = stringResource(R.string.learned),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(10.dp))
            } else {
                Spacer(Modifier.width(14.dp))
            }
            ArabicText(
                text = name.arabic,
                fontSize = ArabicSize.Row,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
