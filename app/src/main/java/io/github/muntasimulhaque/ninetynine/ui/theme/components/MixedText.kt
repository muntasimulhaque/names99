package io.github.muntasimulhaque.ninetynine.ui.theme.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.times
import io.github.muntasimulhaque.ninetynine.ui.theme.ArabicFamily

private val ARABIC_RUN = Regex(
    "[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]+"
)

/**
 * How much larger an inline Arabic run is set than the Latin around it.
 *
 * HAFS's body height is 0.346 em against Spectral's 0.450 x-height, so matched
 * nominal sizes leave the Arabic looking smaller than its neighbours. 0.450 /
 * 0.346 puts the two bodies level, which is the right anchor for Arabic inside
 * a run of Latin. The name page's 2.17 is a display pairing and would shout in
 * running text.
 */
private const val ArabicSpanScale = 1.30f

/** Latin text where embedded Arabic runs are switched to the bundled Arabic typeface. */
@Composable
fun MixedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val hasArabic = remember(text) { ARABIC_RUN.containsMatchIn(text) }
    val annotated = remember(text) {
        val shaped = text.forArabicFont()
        buildAnnotatedString {
            append(shaped)
            ARABIC_RUN.findAll(shaped).forEach { match ->
                // The weight must be pinned, not inherited. Both call sites
                // that matter set a SemiBold slot, and ArabicFamily declares
                // only Normal — so Compose's default FontSynthesis.All was
                // synthesising the missing weight, which on API 28+ means
                // Typeface.create(tf, 600) and a fakeBold smear. That thickens
                // harakat toward the base letter, and a machine-widened
                // outline is exactly the derivative artwork the KFGQPC licence
                // forbids.
                //
                // The size is raised because HAFS's body is 77% of Spectral's
                // x-height (0.346 em against 0.450), so matched nominal sizes
                // leave the Arabic looking smaller than the Latin beside it.
                // 0.450/0.346 = 1.30 puts the two bodies level, which is the
                // right anchor for Arabic set inline in a run of Latin — the
                // name page's 2.17 is a display pairing and would shout here.
                addStyle(
                    SpanStyle(
                        fontFamily = ArabicFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = ArabicSpanScale.em,
                    ),
                    match.range.first,
                    match.range.last + 1,
                )
            }
        }
    }
    // A span set larger than its line needs the line to grow with it, or the
    // enlarged Arabic collides with the line above. HAFS declares 1.758 em of
    // ascender-plus-descender, so an Arabic run at [ArabicSpanScale] needs
    // that much of its own size — anything less shaves the harakat.
    val grownLineHeight = remember(style, hasArabic) {
        if (!hasArabic || !style.fontSize.isSpecified) {
            style.lineHeight
        } else {
            val needed = style.fontSize * ArabicSpanScale * 1.80f
            if (style.lineHeight.isSpecified && style.lineHeight > needed) style.lineHeight
            else needed
        }
    }
    Text(
        text = annotated,
        modifier = modifier,
        style = style.copy(lineHeight = grownLineHeight),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
    )
}
