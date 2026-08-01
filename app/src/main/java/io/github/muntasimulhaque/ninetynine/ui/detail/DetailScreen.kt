package io.github.muntasimulhaque.ninetynine.ui.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.muntasimulhaque.ninetynine.R
import io.github.muntasimulhaque.ninetynine.data.Name
import io.github.muntasimulhaque.ninetynine.ui.NamesViewModel
import io.github.muntasimulhaque.ninetynine.ui.share.ShareSheet
import io.github.muntasimulhaque.ninetynine.ui.theme.Motion
import io.github.muntasimulhaque.ninetynine.ui.theme.components.ArabicText
import io.github.muntasimulhaque.ninetynine.ui.theme.components.BackButton
import io.github.muntasimulhaque.ninetynine.ui.theme.components.LearnedButton
import io.github.muntasimulhaque.ninetynine.ui.theme.components.MixedText
import io.github.muntasimulhaque.ninetynine.ui.theme.components.ScreenLabel
import io.github.muntasimulhaque.ninetynine.ui.theme.rememberHaptics
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

// The reading measure sits in from the page edges. The prev/next footer does
// not — it belongs to the edges themselves, so it bleeds back out through this
// inset (see NamePage).
private val PageInset = 28.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: NamesViewModel,
    startNumber: Int,
    onBack: () -> Unit,
) {
    val names by viewModel.names.collectAsStateWithLifecycle()
    val learned by viewModel.learned.collectAsStateWithLifecycle()
    val bookmarked by viewModel.bookmarked.collectAsStateWithLifecycle()
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
    val haptics = rememberHaptics()

    // A featherweight tick as each page settles — like a bead slipping past.
    LaunchedEffect(pagerState) {
        var first = true
        snapshotFlow { pagerState.currentPage }.collect {
            if (first) first = false else haptics.tick()
        }
    }

    if (showShare) {
        ShareSheet(name = current, onDismiss = { showShare = false })
    }

    // A single calm fade as the screen settles in.
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val enterAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(Motion.CALM),
        label = "detailEnter",
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    ScreenLabel(stringResource(R.string.detail_counter, current.number))
                },
                navigationIcon = { BackButton(onBack) },
                // Keeping, then sending: the inward act sits inside, and Share
                // keeps the edge it has always had.
                actions = {
                    BookmarkAction(
                        bookmarked = current.number in bookmarked,
                        onToggle = {
                            viewModel.setBookmarked(current.number, current.number !in bookmarked)
                        },
                    )
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
                // Pages dim slightly while in motion, then settle to full
                // presence. Read inside the layer block so a swipe redraws
                // rather than recomposing every visible page each frame.
                modifier = Modifier.graphicsLayer {
                    val offset =
                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                            .absoluteValue.coerceIn(0f, 1f)
                    alpha = 1f - offset * 0.3f
                },
            )
        }
    }
}

/**
 * Keeping a name, from the bar rather than the foot of the page.
 *
 * The page is one scroll container, so the footer travels with the text — on a
 * long meaning it is well below the fold at exactly the moment a name strikes
 * you. The bar does not move.
 *
 * The pop and the haptic are lifted from [LearnedButton] deliberately: the app
 * has two per-name toggles on two different axes, and they should at least feel
 * like they were made by the same hand.
 */
@Composable
private fun BookmarkAction(bookmarked: Boolean, onToggle: () -> Unit) {
    val haptics = rememberHaptics()

    val scale = remember { Animatable(1f) }
    val seeded = remember { mutableStateOf(false) }
    LaunchedEffect(bookmarked) {
        if (!seeded.value) {
            seeded.value = true
        } else {
            scale.snapTo(0.94f)
            scale.animateTo(1f, Motion.lively())
        }
    }

    // The button is named once and never changes; what changes is the state
    // announced after it. Set on the button, which is the node TalkBack focuses.
    val state = stringResource(if (bookmarked) R.string.bookmarked else R.string.not_bookmarked)
    IconButton(
        onClick = {
            haptics.confirm()
            onToggle()
        },
        modifier = Modifier.semantics { stateDescription = state },
    ) {
        Icon(
            imageVector = if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
            contentDescription = stringResource(R.string.cd_bookmark),
            tint = if (bookmarked) MaterialTheme.colorScheme.secondary else LocalContentColor.current,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
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
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    // Single scrollable page: the controls scroll with the content, but a
    // weighted spacer pushes them to just above the system bar whenever the
    // content is shorter than the screen.
    BoxWithConstraints(modifier.fillMaxSize()) {
        val minPageHeight = maxHeight
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = PageInset),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.defaultMinSize(minHeight = minPageHeight),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(30.dp))
                ArabicText(
                    text = name.arabic,
                    fontSize = 52.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(14.dp))
                // Kept a clear step below the Arabic — the same proportion the
                // share card and the widget hold (roughly half the Arabic size).
                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.displaySmall,
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
                Spacer(Modifier.height(28.dp))
                LearnedButton(learned = learned, onToggle = onToggleLearned)
                Spacer(Modifier.height(10.dp))
                Row(
                    // Widened back out through the page inset so the chevrons
                    // land on the same vertical line as the back and share
                    // icons in the top bar, instead of floating in toward the
                    // middle of the page. Safe to overflow: the padding sits
                    // inside the scroll container, so this only reaches the
                    // viewport's own edge — nothing clips it.
                    modifier = Modifier
                        .fillMaxWidth()
                        .layout { measurable, constraints ->
                            val bleed = PageInset.roundToPx()
                            val placeable = measurable.measure(
                                constraints.copy(
                                    minWidth = constraints.minWidth + bleed * 2,
                                    maxWidth = constraints.maxWidth + bleed * 2,
                                )
                            )
                            layout(constraints.maxWidth, placeable.height) {
                                placeable.place(-bleed, 0)
                            }
                        },
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

        // The page melts into the paper at the fold while more remains below —
        // a quiet invitation to keep reading. Gone once the end is reached.
        val fadeAlpha by animateFloatAsState(
            targetValue = if (scrollState.canScrollForward) 1f else 0f,
            animationSpec = tween(Motion.QUICK),
            label = "edgeFade",
        )
        val paper = MaterialTheme.colorScheme.background
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(64.dp)
                .graphicsLayer { alpha = fadeAlpha }
                .background(
                    Brush.verticalGradient(
                        0f to paper.copy(alpha = 0f),
                        1f to paper,
                    )
                ),
        )
    }
}
