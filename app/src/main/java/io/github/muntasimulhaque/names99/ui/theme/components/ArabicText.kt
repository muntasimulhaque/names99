package io.github.muntasimulhaque.names99.ui.theme.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import io.github.muntasimulhaque.names99.ui.theme.ArabicFamily

/** Arabic text in the bundled Noto Naskh Arabic typeface with generous line height for diacritics. */
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
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontFamily = ArabicFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = if (lineHeight != TextUnit.Unspecified) lineHeight else fontSize * 1.6f,
    )
}
