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
    Font(R.font.spectral_light, FontWeight.Light),
    Font(R.font.spectral_regular, FontWeight.Normal),
    Font(R.font.spectral_medium, FontWeight.Medium),
    Font(R.font.spectral_semibold, FontWeight.SemiBold),
    Font(R.font.spectral_italic, FontWeight.Normal, FontStyle.Italic),
)

/*
 * The type system carries the whole hierarchy: large jumps in size, a light
 * weight for display text, semibold for headings, italics for the poetic
 * lines, and wide-tracked small caps for labels. Color only accents it.
 */
private val BaseTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Light,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Light,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.25).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Light,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp,
    ),
    // Tracked small caps: use with .uppercase() for overlines like "NAME OF THE DAY".
    labelMedium = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.8.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = SpectralFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp,
    ),
)

private fun TextStyle.scaled(factor: Float): TextStyle =
    copy(fontSize = fontSize * factor, lineHeight = lineHeight * factor)

/** Typography with every size multiplied by the user's text-scale preference. */
fun appTypography(scale: Float): Typography {
    if (scale == 1f) return BaseTypography
    return Typography(
        displayLarge = BaseTypography.displayLarge.scaled(scale),
        displayMedium = BaseTypography.displayMedium.scaled(scale),
        displaySmall = BaseTypography.displaySmall.scaled(scale),
        headlineLarge = BaseTypography.headlineLarge.scaled(scale),
        headlineMedium = BaseTypography.headlineMedium.scaled(scale),
        headlineSmall = BaseTypography.headlineSmall.scaled(scale),
        titleLarge = BaseTypography.titleLarge.scaled(scale),
        titleMedium = BaseTypography.titleMedium.scaled(scale),
        titleSmall = BaseTypography.titleSmall.scaled(scale),
        bodyLarge = BaseTypography.bodyLarge.scaled(scale),
        bodyMedium = BaseTypography.bodyMedium.scaled(scale),
        bodySmall = BaseTypography.bodySmall.scaled(scale),
        labelLarge = BaseTypography.labelLarge.scaled(scale),
        labelMedium = BaseTypography.labelMedium.scaled(scale),
        labelSmall = BaseTypography.labelSmall.scaled(scale),
    )
}
