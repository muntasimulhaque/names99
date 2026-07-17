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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.muntasimulhaque.names99.MainActivity
import io.github.muntasimulhaque.names99.data.NamesRepository

class DailyNameWidget : GlanceAppWidget() {

    companion object {
        // Responsive height buckets: show only as many lines as fit completely,
        // so nothing is ever clipped at any widget size.
        private val COMPACT = DpSize(110.dp, 40.dp) // Arabic only
        private val MEDIUM = DpSize(110.dp, 90.dp) // + transliteration
        private val TALL = DpSize(110.dp, 140.dp) // + meaning
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(COMPACT, MEDIUM, TALL))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val name = NamesRepository.dailyName(context)
        provideContent {
            val height = LocalSize.current.height
            val showTransliteration = height >= MEDIUM.height
            val showTitle = height >= TALL.height
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(Color(0xFF1F4E42)))
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
                    text = name.arabic,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFD4B45A)),
                        fontSize = if (showTransliteration) 26.sp else 22.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )
                if (showTransliteration) {
                    Text(
                        text = name.transliteration,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFF2EDE2)),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(top = 4.dp)
                    )
                }
                if (showTitle) {
                    Text(
                        text = name.title,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFBFD5CB)),
                            fontSize = 12.sp,
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
