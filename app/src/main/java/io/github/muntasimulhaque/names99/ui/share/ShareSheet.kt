package io.github.muntasimulhaque.names99.ui.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.ui.theme.HeroContainer
import io.github.muntasimulhaque.names99.ui.theme.HeroGold
import io.github.muntasimulhaque.names99.ui.theme.HeroSubtext
import io.github.muntasimulhaque.names99.ui.theme.HeroText
import io.github.muntasimulhaque.names99.ui.theme.components.ArabicText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(name: Name, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var sharing by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.share_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(16.dp))
            // The card grows to hold the complete meaning — never an ellipsis.
            // Long cards scroll in this preview; the export is the full card.
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Everything inside this box is recorded into the graphics layer,
                // so it can be exported as a bitmap while drawing normally on screen.
                Box(
                    modifier = Modifier.drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
                ) {
                    ShareCard(name = name, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                enabled = !sharing,
                onClick = {
                    scope.launch {
                        sharing = true
                        runCatching {
                            val bitmap = graphicsLayer.toImageBitmap()
                            shareNameImage(context, bitmap, name)
                        }
                        sharing = false
                        onDismiss()
                    }
                },
            ) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Spacer(Modifier.padding(start = 8.dp))
                Text(stringResource(R.string.share_image))
            }
        }
    }
}

/**
 * The exported card: deep emerald + gold, identical to the widget/hero
 * identity, with a fine gold frame inside — like a printed plate.
 */
@Composable
private fun ShareCard(name: Name, modifier: Modifier = Modifier) {
    // One hairline gold rule serves the whole plate: the frame around the card
    // and the seal around the mark are drawn with the identical stroke.
    val frameGold = HeroGold.copy(alpha = 0.4f)
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = HeroContainer),
    ) {
        Box(Modifier.padding(10.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = frameGold,
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ArabicText(
                    text = stringResource(R.string.basmala),
                    fontSize = 15.sp,
                    color = HeroSubtext,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                ArabicText(
                    text = name.arabic,
                    fontSize = 50.sp,
                    color = HeroGold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.displaySmall,
                    color = HeroText,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = name.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    color = HeroSubtext,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = name.meaning,
                    // The whole meaning, always; very long ones step down a size.
                    style = if (name.meaning.length > 450) MaterialTheme.typography.bodySmall
                    else MaterialTheme.typography.bodyMedium,
                    color = HeroText,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                // Maker's mark: the app's gold ٩٩ struck as a seal — the circle
                // reads it as the logo rather than a second "99" in the line.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .border(width = 1.dp, color = frameGold, shape = CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_mark_99),
                            contentDescription = null,
                            modifier = Modifier.height(12.dp),
                        )
                    }
                    Spacer(Modifier.width(9.dp))
                    Text(
                        text = stringResource(R.string.app_title).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = HeroSubtext,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private suspend fun shareNameImage(context: Context, bitmap: ImageBitmap, name: Name) {
    val uri = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "shared_images").apply { mkdirs() }
        val file = File(dir, "name_${name.number}.png")
        FileOutputStream(file).use {
            bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}
