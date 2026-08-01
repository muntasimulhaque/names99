package io.github.muntasimulhaque.ninetynine.ui.theme.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import io.github.muntasimulhaque.ninetynine.ui.theme.ArabicFamily
import io.github.muntasimulhaque.ninetynine.ui.theme.LocalTextScale

/**
 * KFGQPC HAFS encodes madda the mushaf way (alef + combining maddah, U+0627
 * U+0653) and has no glyph for precomposed U+0622, so decompose before
 * rendering. Keeps the assets NFC-clean.
 */
fun String.forArabicFont(): String = replace("\u0622", "\u0627\u0653")

/**
 * Arabic text in the bundled mushaf typeface with generous line height for
 * diacritics. Sizes are given explicitly rather than taken from the type
 * scale, so the reader's text-size preference is applied here by hand.
 */
@Composable
fun ArabicText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
) {
    val shaped = remember(text) { text.forArabicFont() }
    val size = fontSize * LocalTextScale.current
    Text(
        text = shaped,
        modifier = modifier,
        color = color,
        fontSize = size,
        fontFamily = ArabicFamily,
        textAlign = textAlign,
        lineHeight = size * 1.7f,
    )
}
