package io.github.muntasimulhaque.names99.ui.memorize

import android.app.Application
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.theme.HeroContainer
import io.github.muntasimulhaque.names99.ui.theme.HeroGold
import io.github.muntasimulhaque.names99.ui.theme.HeroSubtext
import io.github.muntasimulhaque.names99.ui.theme.HeroText
import io.github.muntasimulhaque.names99.ui.theme.Motion
import io.github.muntasimulhaque.names99.ui.theme.components.ArabicText
import io.github.muntasimulhaque.names99.ui.theme.components.BackButton
import io.github.muntasimulhaque.names99.ui.theme.components.HairlineProgress
import io.github.muntasimulhaque.names99.ui.theme.components.paperTopBarColors
import io.github.muntasimulhaque.names99.ui.theme.components.ScreenLabel
import io.github.muntasimulhaque.names99.ui.theme.rememberHaptics
import io.github.muntasimulhaque.names99.util.DeckBuilder
import kotlinx.coroutines.launch

/** Session state for one flashcard run; survives rotation with the ViewModel. */
class FlashcardsViewModel(application: Application) : AndroidViewModel(application) {

    var deck by mutableStateOf<List<Int>>(emptyList()); private set
    var index by mutableIntStateOf(0); private set
    var flipped by mutableStateOf(false); private set
    var done by mutableStateOf(false); private set
    private var lastInclude: Boolean? = null

    fun ensureDeck(names: List<Name>, learned: Set<Int>, includeLearned: Boolean) {
        if (names.isEmpty()) return
        if (deck.isNotEmpty() && lastInclude == includeLearned) return
        deck = DeckBuilder.build(names, learned, includeLearned)
        lastInclude = includeLearned
        index = 0
        flipped = false
        done = false
    }

    fun flip() {
        flipped = !flipped
    }

    fun advance() {
        flipped = false
        if (index < deck.lastIndex) index++ else done = true
    }

    fun restart(names: List<Name>, learned: Set<Int>, includeLearned: Boolean) {
        deck = emptyList()
        lastInclude = null
        ensureDeck(names, learned, includeLearned)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    viewModel: NamesViewModel,
    onBack: () -> Unit,
) {
    val session: FlashcardsViewModel = viewModel()
    val names by viewModel.names.collectAsStateWithLifecycle()
    val learned by viewModel.learned.collectAsStateWithLifecycle()
    val includeLearned by viewModel.includeLearned.collectAsStateWithLifecycle()

    LaunchedEffect(names, includeLearned) {
        session.ensureDeck(names, learned, includeLearned)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = paperTopBarColors(),
                title = {
                    ScreenLabel(
                        if (session.deck.isNotEmpty() && !session.done) {
                            stringResource(
                                R.string.card_x_of_y,
                                session.index + 1,
                                session.deck.size,
                            )
                        } else {
                            stringResource(R.string.flashcards)
                        }
                    )
                },
                navigationIcon = {
                    BackButton(onBack)
                },
                actions = {
                    // A named menu, not two mute icons: the deck options say what
                    // they do, and "include learned" can show that it is on.
                    DeckMenu(
                        includeLearned = includeLearned,
                        onToggleIncludeLearned = {
                            viewModel.setIncludeLearned(!includeLearned)
                        },
                        onReshuffle = { session.restart(names, learned, includeLearned) },
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when {
                names.isEmpty() -> Unit
                session.deck.isEmpty() -> AllLearnedContent(
                    onReviewLearned = { viewModel.setIncludeLearned(true) },
                    onBack = onBack,
                )
                session.done -> DeckDoneContent(
                    onStartAgain = { session.restart(names, learned, includeLearned) },
                )
                else -> {
                    val name = names.firstOrNull { it.number == session.deck[session.index] }
                        ?: return@Column
                    val scope = rememberCoroutineScope()
                    val haptics = rememberHaptics()

                    // Horizontal offset of the current card; a fresh Animatable
                    // per card so each one starts centered.
                    val offsetX = remember(session.deck, session.index) { Animatable(0f) }
                    var cardWidth by remember { mutableFloatStateOf(0f) }

                    fun commit(know: Boolean) {
                        if (offsetX.isRunning && offsetX.targetValue != 0f) return
                        scope.launch {
                            haptics.confirm()
                            val target = (if (know) 1.3f else -1.3f) * cardWidth
                            offsetX.animateTo(target, tween(240))
                            // A review pass only ever adds. "Still learning" must
                            // not quietly delete a tick the reader already earned.
                            if (know && name.number !in learned) {
                                viewModel.setLearned(name.number, true)
                            }
                            session.advance()
                        }
                    }

                    HairlineProgress(
                        progress = (session.index + 1) / session.deck.size.toFloat(),
                    )
                    // The card keeps card proportions instead of stretching into
                    // a full-height plane; it sits centred in whatever is left.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        SwipeFlipCard(
                            name = name,
                            flipped = session.flipped,
                            onFlip = {
                                haptics.tick()
                                session.flip()
                            },
                            offsetX = offsetX,
                            onDragCommit = ::commit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 460.dp)
                                .fillMaxHeight()
                                .onSizeChanged { cardWidth = it.width.toFloat() },
                        )
                    }
                    Text(
                        text = if (session.index == 0) stringResource(R.string.swipe_hint) else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { commit(false) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp),
                        ) {
                            Text(stringResource(R.string.still_learning))
                        }
                        Button(
                            onClick = { commit(true) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp),
                        ) {
                            Text(stringResource(R.string.i_know_it))
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

/** Deck options, named: what each one does, and whether it is already on. */
@Composable
private fun DeckMenu(
    includeLearned: Boolean,
    onToggleIncludeLearned: () -> Unit,
    onReshuffle: () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.cd_more),
            )
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.include_learned)) },
                onClick = {
                    onToggleIncludeLearned()
                    open = false
                },
                // The state belongs on the row itself: it is the focusable
                // node, so semantics on the tick box never reach a reader.
                modifier = Modifier.semantics {
                    role = Role.Checkbox
                    toggleableState =
                        if (includeLearned) ToggleableState.On else ToggleableState.Off
                },
                leadingIcon = { OptionCheck(checked = includeLearned) },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.reshuffle)) },
                onClick = {
                    onReshuffle()
                    open = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Shuffle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}

/**
 * An empty box in the same ink as the menu's icons, so the option reads as
 * something you can turn on even while it is off; the tick alone is gold.
 * Purely visual — the row above carries the state for screen readers.
 */
@Composable
private fun OptionCheck(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(4.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(13.dp),
            )
        }
    }
}

@Composable
private fun SwipeFlipCard(
    name: Name,
    flipped: Boolean,
    onFlip: () -> Unit,
    offsetX: Animatable<Float, *>,
    onDragCommit: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    // Each card arrives with a soft rise (keyed to the card's own offset state).
    val appear = remember(offsetX) { Animatable(0f) }
    LaunchedEffect(offsetX) {
        appear.animateTo(1f, tween(Motion.GENTLE, easing = Motion.Settle))
    }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(Motion.GENTLE),
        label = "flip",
    )

    Card(
        onClick = onFlip,
        modifier = modifier
            .graphicsLayer {
                val w = size.width.coerceAtLeast(1f)
                val leaving = offsetX.value / (w * 1.2f)
                translationX = offsetX.value
                rotationZ = (offsetX.value / w) * 8f
                alpha = (appear.value * (1f - leaving * leaving)).coerceIn(0f, 1f)
                scaleX = 0.96f + 0.04f * appear.value
                scaleY = 0.96f + 0.04f * appear.value
                translationY = (1f - appear.value) * 24.dp.toPx()
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .pointerInput(offsetX) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = size.width * 0.3f
                        when {
                            offsetX.value > threshold -> onDragCommit(true)
                            offsetX.value < -threshold -> onDragCommit(false)
                            else -> scope.launch { offsetX.animateTo(0f, Motion.soft()) }
                        }
                    },
                    onDragCancel = {
                        scope.launch { offsetX.animateTo(0f, Motion.soft()) }
                    },
                ) { change, amount ->
                    change.consume()
                    scope.launch { offsetX.snapTo(offsetX.value + amount) }
                }
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (rotation <= 90f) HeroContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = if (rotation <= 90f) null
        else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        if (rotation <= 90f) {
            // Front: the name itself, set like the share card.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ArabicText(
                    text = name.arabic,
                    fontSize = 44.sp,
                    color = HeroGold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.displaySmall,
                    color = HeroText,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.tap_to_flip),
                    style = MaterialTheme.typography.bodySmall,
                    color = HeroSubtext,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            // Back: title + meaning (counter-rotated so it reads correctly).
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = name.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = name.meaning,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

@Composable
private fun AllLearnedContent(
    onReviewLearned: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ArabicText(
            text = "٩٩",
            fontSize = 44.sp,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.all_learned_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.all_learned_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onReviewLearned) {
            Text(stringResource(R.string.review_learned))
        }
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.back_to_memorize))
        }
    }
}

@Composable
private fun DeckDoneContent(onStartAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.deck_done),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStartAgain) {
            Text(stringResource(R.string.start_again))
        }
    }
}
