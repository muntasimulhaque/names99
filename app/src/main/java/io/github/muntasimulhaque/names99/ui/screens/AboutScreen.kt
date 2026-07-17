package io.github.muntasimulhaque.names99.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Prefs

private const val BLOG_URL = "https://muntasimulhaque.bearblog.dev/99-names/"
private const val SOURCE_PDF_URL =
    "https://bear-images.sfo2.cdn.digitaloceanspaces.com/muntasimulhaque/ninety-nine-names-1_compressed.pdf"
private const val REPO_URL = "https://github.com/muntasimulhaque/99-names-app"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController, prefs: Prefs) {
    val context = LocalContext.current
    val textScale by prefs.textScale.collectAsState(initial = 1f)
    val intro = remember {
        context.assets.open("intro.txt").bufferedReader().use { it.readText() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.basmala),
                fontSize = (24 * textScale).sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            intro.split("\n\n").forEach { para ->
                if (para.startsWith("##")) {
                    Text(
                        text = para.trimStart('#').trim(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp)
                    )
                    return@forEach
                }
                val isQuote = para.startsWith("> ")
                Text(
                    text = para.removePrefix("> ").trim('"').let {
                        if (isQuote) "“$it”" else it
                    },
                    style = if (isQuote) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.bodyLarge,
                    fontStyle = if (isQuote) FontStyle.Italic else FontStyle.Normal,
                    fontSize = (if (isQuote) 17 else 16).let { (it * textScale).sp },
                    lineHeight = (26 * textScale).sp,
                    color = if (isQuote) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground,
                    textAlign = if (isQuote) TextAlign.Center else TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text(
                text = stringResource(R.string.about_dua),
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                fontSize = (16 * textScale).sp,
                lineHeight = (26 * textScale).sp
            )
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.about_attribution),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            TextButton(onClick = { context.openUrl(SOURCE_PDF_URL) }) { Text(stringResource(R.string.source_pdf)) }
            TextButton(onClick = { context.openUrl(BLOG_URL) }) { Text(stringResource(R.string.read_blog)) }
            TextButton(onClick = { context.openUrl(REPO_URL) }) { Text(stringResource(R.string.foss_line)) }
            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun android.content.Context.openUrl(url: String) {
    runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}
