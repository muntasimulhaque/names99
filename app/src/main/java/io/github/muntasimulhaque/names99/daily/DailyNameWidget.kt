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
import androidx.glance.color.ColorProvider
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import io.github.muntasimulhaque.names99.MainActivity
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.NamesRepository
import io.github.muntasimulhaque.names99.util.DailyName

class DailyNameWidget : GlanceAppWidget() {

    companion object {
        // Responsive height buckets: show only as many lines as fit completely,
        // so nothing is ever clipped at any widget size.
        private val COMPACT = DpSize(110.dp, 40.dp) // Arabic only
        private val MEDIUM = DpSize(110.dp, 90.dp) // + transliteration
        private val TALL = DpSize(110.dp, 140.dp) // + overline & meaning
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(COMPACT, MEDIUM, TALL))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val names = NamesRepository.load(context)
        val name = names.firstOrNull { it.number == DailyName.numberFor(System.currentTimeMillis()) }
            ?: return
        provideContent {
            val height = LocalSize.current.height
            val showTransliteration = height >= MEDIUM.height
            val showTitle = height >= TALL.height

            // Glance cannot load bundled fonts, so Arabic and Latin both fall
            // back to the system serif — which matches the app's book-like feel.
            val serif = FontFamily("serif")
            val background = ColorProvider(day = Color(0xFF1F4E42), night = Color(0xFF191611))
            val gold = ColorProvider(day = Color(0xFFD4B45A), night = Color(0xFFD4B45A))
            val textColor = ColorProvider(day = Color(0xFFF2EDE2), night = Color(0xFFEAE2D1))
            val subtextColor = ColorProvider(day = Color(0xFFBFD5CB), night = Color(0xFFA79B86))

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
                if (showTitle) {
                    Text(
                        text = context.getString(R.string.widget_label).uppercase(),
                        maxLines = 1,
                        style = TextStyle(
                            color = gold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = serif,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(bottom = 6.dp)
                    )
                }
                Text(
                    text = name.arabic,
                    maxLines = 1,
                    style = TextStyle(
                        color = gold,
                        fontSize = if (showTransliteration) 26.sp else 22.sp,
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
                            fontSize = 15.sp,
                            fontFamily = serif,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(top = 4.dp)
                    )
                }
                if (showTitle) {
                    Text(
                        text = name.title,
                        maxLines = 1,
                        style = TextStyle(
                            color = subtextColor,
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = serif,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

class DailyNameWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyNameWidget()
}
