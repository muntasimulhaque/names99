package io.github.muntasimulhaque.names99.ui.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.share.ShareSheet
import io.github.muntasimulhaque.names99.ui.theme.components.ArabicText
import io.github.muntasimulhaque.names99.ui.theme.components.MixedText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: NamesViewModel,
    startNumber: Int,
    onBack: () -> Unit,
) {
    val names by viewModel.names.collectAsStateWithLifecycle()
    val learned by viewModel.learned.collectAsStateWithLifecycle()
    var showShare by remember { mutableStateOf(false) }

    if (names.isEmpty()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {},
                    navigationIcon = { BackButton(onBack) },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    val startIndex = remember(names) {
        names.indexOfFirst { it.number == startNumber }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(initialPage = startIndex) { names.size }
    val current = names[pagerState.currentPage]

    if (showShare) {
        ShareSheet(name = current, onDismiss = { showShare = false })
    }

    // A single calm fade as the page settles in.
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val enterAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(450),
        label = "detailEnter",
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.detail_counter, current.number).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    IconButton(onClick = { showShare = true }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = stringResource(R.string.cd_share),
                        )
                    }
                },
            )
        },
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .graphicsLayer { alpha = enterAlpha },
        ) { page ->
            NamePage(
                name = names[page],
                learned = names[page].number in learned,
                onToggleLearned = {
                    val number = names[page].number
                    viewModel.setLearned(number, number !in learned)
                },
                pagerState = pagerState,
                page = page,
                previousLabel = names.getOrNull(page - 1)?.transliteration,
                nextLabel = names.getOrNull(page + 1)?.transliteration,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_back),
        )
    }
}

@Composable
private fun NamePage(
    name: Name,
    learned: Boolean,
    onToggleLearned: () -> Unit,
    pagerState: PagerState,
    page: Int,
    previousLabel: String?,
    nextLabel: String?,
) {
    val scope = rememberCoroutineScope()

    // Single scrollable page: the controls scroll with the content, but a
    // weighted spacer pushes them to just above the system bar whenever the
    // content is shorter than the screen.
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val minPageHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.defaultMinSize(minHeight = minPageHeight),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(36.dp))
                ArabicText(
                    text = name.arabic,
                    fontSize = 72.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = name.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    text = name.meaning,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.widthIn(max = 560.dp),
                )
                if (name.note != null) {
                    Spacer(Modifier.height(26.dp))
                    Column(
                        modifier = Modifier.widthIn(max = 560.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = stringResource(R.string.note_label).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        Spacer(Modifier.height(8.dp))
                        MixedText(
                            text = name.note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(24.dp))
                FilterChip(
                    selected = learned,
                    onClick = onToggleLearned,
                    label = {
                        Text(stringResource(if (learned) R.string.learned else R.string.mark_learned))
                    },
                    leadingIcon = if (learned) {
                        {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    } else null,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (previousLabel != null) {
                        TextButton(onClick = { scope.launch { pagerState.animateScrollToPage(page - 1) } }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                            Text(previousLabel, fontStyle = FontStyle.Italic)
                        }
                    } else {
                        Spacer(Modifier.widthIn(min = 48.dp))
                    }
                    if (nextLabel != null) {
                        TextButton(onClick = { scope.launch { pagerState.animateScrollToPage(page + 1) } }) {
                            Text(nextLabel, fontStyle = FontStyle.Italic)
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        }
                    } else {
                        Spacer(Modifier.widthIn(min = 48.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
