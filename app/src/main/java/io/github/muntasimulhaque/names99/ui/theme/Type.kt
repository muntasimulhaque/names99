package io.github.muntasimulhaque.names99.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.muntasimulhaque.names99.R

/** Arabic everywhere in the app: Noto Naskh Arabic (SIL Open Font License). */
val ArabicFamily = FontFamily(
    Font(R.font.notonaskharabic_regular, FontWeight.Normal),
    Font(R.font.notonaskharabic_bold, FontWeight.Bold),
)

/** Latin text identity (SIL Open Font License). */
val SpectralFamily = FontFamily(
    Font(R.font.spectral_regular, FontWeight.Normal),
    Font(R.font.spectral_medium, FontWeight.Medium),
    Font(R.font.spectral_semibold, FontWeight.SemiBold),
    Font(R.font.spectral_italic, FontWeight.Normal, FontStyle.Italic),
)

private val BaseTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 27.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 23.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 27.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 23.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.3.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
)

private fun TextStyle.scaled(factor: Float): TextStyle =
    copy(fontSize = fontSize * factor, lineHeight = lineHeight * factor)

/** Typography with every size multiplied by the user's text-scale preference. */
fun appTypography(scale: Float): Typography {
    if (scale == 1f) return BaseTypography
    return BaseTypography.copy(
        displayMedium = BaseTypography.displayMedium.scaled(scale),
        headlineSmall = BaseTypography.headlineSmall.scaled(scale),
        titleLarge = BaseTypography.titleLarge.scaled(scale),
        titleMedium = BaseTypography.titleMedium.scaled(scale),
        bodyLarge = BaseTypography.bodyLarge.scaled(scale),
        bodyMedium = BaseTypography.bodyMedium.scaled(scale),
        labelLarge = BaseTypography.labelLarge.scaled(scale),
        labelMedium = BaseTypography.labelMedium.scaled(scale),
    )
}
