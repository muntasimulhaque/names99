package io.github.muntasimulhaque.names99.daily

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.muntasimulhaque.names99.MainActivity
import io.github.muntasimulhaque.names99.data.NamesRepository
import io.github.muntasimulhaque.names99.util.DailyName

class DailyNameWidget : GlanceAppWidget() {

    companion object {
        // Responsive height buckets: show only as many lines as fit completely,
        // sized so even the longest title (#39, 71 chars) never truncates.
        private val COMPACT = DpSize(110.dp, 40.dp) // Arabic only
        private val MEDIUM = DpSize(110.dp, 90.dp) // + transliteration
        private val TALL = DpSize(110.dp, 140.dp) // + title (wrapping)
        private val XTALL = DpSize(110.dp, 180.dp) // everything, larger

        /**
         * The system serif (Noto Naskh) misplaces the marks of the vocalized
         * الله over the lam-heh joint — the very bug that once forced stripping
         * them app-wide. The app's bundled HAFS renders it correctly, but the
         * widget and notification draw with system fonts, so they show the
         * plain form for this one word.
         */
        fun systemFontSafeArabic(text: String): String =
            text.replace("اللَّه", "الله")
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(COMPACT, MEDIUM, TALL, XTALL))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val names = NamesRepository.load(context)
        val name = names.firstOrNull { it.number == DailyName.numberFor(System.currentTimeMillis()) }
            ?: return
        provideContent {
            val height = LocalSize.current.height
            val showTransliteration = height >= MEDIUM.height
            val showTitle = height >= TALL.height
            val roomy = height >= XTALL.height

            // Glance cannot load bundled fonts, so Arabic and Latin fall back
            // to the system serif — which matches the app's book-like feel.
            val serif = FontFamily("serif")
            // One identity on every home screen: the emerald-and-gold of the
            // hero and share cards, deliberately NOT day/night switched.
            val background = ColorProvider(Color(0xFF1F4E42))
            val gold = ColorProvider(Color(0xFFD4B45A))
            val textColor = ColorProvider(Color(0xFFF2EDE2))
            val subtextColor = ColorProvider(Color(0xFFBFD5CB))

            val arabicSize = when {
                roomy -> 38.sp
                showTitle -> 32.sp
                showTransliteration -> 30.sp
                else -> 22.sp
            }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(background)
                    .cornerRadius(20.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(
                        actionStartActivity<MainActivity>(
                            actionParametersOf(
                                ActionParameters.Key<Int>(MainActivity.EXTRA_NAME_NUMBER) to name.number
                            )
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = systemFontSafeArabic(name.arabic),
                    maxLines = 1,
                    style = TextStyle(
                        color = gold,
                        fontSize = arabicSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = serif,
                        textAlign = TextAlign.Center
                    )
                )
                if (showTransliteration) {
                    Text(
                        text = name.transliteration,
                        maxLines = 1,
                        style = TextStyle(
                            color = textColor,
                            fontSize = if (roomy) 18.sp else 16.sp,
                            fontFamily = serif,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(top = 4.dp)
                    )
                }
                if (showTitle) {
                    Text(
                        text = name.title,
                        maxLines = 3,
                        style = TextStyle(
                            color = subtextColor,
                            fontSize = if (roomy) 14.sp else 12.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = serif,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

class DailyNameWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyNameWidget()
}
