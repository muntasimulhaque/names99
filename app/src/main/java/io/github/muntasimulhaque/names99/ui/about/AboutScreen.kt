package io.github.muntasimulhaque.names99.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.ui.theme.components.ArabicText
import io.github.muntasimulhaque.names99.ui.theme.components.MixedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val BLOG_URL = "https://muntasimulhaque.bearblog.dev/99-names/"
private const val SOURCE_PDF_URL =
    "https://bear-images.sfo2.cdn.digitaloceanspaces.com/muntasimulhaque/ninety-nine-names-1_compressed.pdf"
private const val REPO_URL = "https://github.com/muntasimulhaque/99-names-app"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val intro by produceState(initialValue = "") {
        value = withContext(Dispatchers.IO) {
            context.assets.open("intro.txt").bufferedReader().use { it.readText() }
        }
    }

    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val enterAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(450),
        label = "aboutEnter",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .graphicsLayer { alpha = enterAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))
            ArabicText(
                text = stringResource(R.string.basmala),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))
            // intro.txt may be checked out with CRLF endings; normalize before splitting.
            intro.replace("\r\n", "\n").split("\n\n").forEach { rawPara ->
                val para = rawPara.trim()
                when {
                    para.startsWith("##") -> MixedText(
                        text = para.trimStart('#').trim(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .widthIn(max = 560.dp)
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 18.dp),
                    )
                    para.startsWith(">") -> Text(
                        text = para.removePrefix(">").trim(),
                        style = MaterialTheme.typography.titleLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .widthIn(max = 560.dp)
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .padding(bottom = 18.dp),
                    )
                    else -> Text(
                        text = para,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .widthIn(max = 560.dp)
                            .fillMaxWidth()
                            .padding(bottom = 18.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.about_dua),
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.about_attribution),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { context.openUrl(SOURCE_PDF_URL) }) {
                Text(stringResource(R.string.source_pdf))
            }
            TextButton(onClick = { context.openUrl(BLOG_URL) }) {
                Text(stringResource(R.string.read_blog))
            }
            TextButton(onClick = { context.openUrl(REPO_URL) }) {
                Text(stringResource(R.string.foss_line))
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.about_fonts),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
            )
            Spacer(Modifier.height(36.dp))
        }
    }
}

private fun Context.openUrl(url: String) {
    runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}
