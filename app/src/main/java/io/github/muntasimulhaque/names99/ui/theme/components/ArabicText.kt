package io.github.muntasimulhaque.names99.ui.theme.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import io.github.muntasimulhaque.names99.ui.theme.ArabicFamily

/**
 * KFGQPC HAFS encodes madda the mushaf way (alef + combining maddah, U+0627
 * U+0653) and has no glyph for precomposed U+0622, so decompose before
 * rendering. Keeps the assets NFC-clean.
 */
fun String.forArabicFont(): String = replace("\u0622", "\u0627\u0653")

/** Arabic text in the bundled mushaf typeface with generous line height for diacritics. */
@Composable
fun ArabicText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    val shaped = remember(text) { text.forArabicFont() }
    Text(
        text = shaped,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontFamily = ArabicFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = if (lineHeight != TextUnit.Unspecified) lineHeight else fontSize * 1.7f,
    )
}
