package io.github.muntasimulhaque.ninetynine.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.muntasimulhaque.ninetynine.R
import io.github.muntasimulhaque.ninetynine.data.Name
import io.github.muntasimulhaque.ninetynine.ui.NamesViewModel
import io.github.muntasimulhaque.ninetynine.ui.theme.HeroContainer
import io.github.muntasimulhaque.ninetynine.ui.theme.HeroGold
import io.github.muntasimulhaque.ninetynine.ui.theme.HeroSubtext
import io.github.muntasimulhaque.ninetynine.ui.theme.HeroText
import io.github.muntasimulhaque.ninetynine.ui.theme.Motion
import io.github.muntasimulhaque.ninetynine.ui.theme.components.ArabicSize
import io.github.muntasimulhaque.ninetynine.ui.theme.components.ArabicText
import io.github.muntasimulhaque.ninetynine.ui.theme.components.FitText
import io.github.muntasimulhaque.ninetynine.ui.theme.components.NameListItem
import io.github.muntasimulhaque.ninetynine.ui.theme.components.NameRowInset
import io.github.muntasimulhaque.ninetynine.ui.theme.components.PageMessage
import io.github.muntasimulhaque.ninetynine.ui.theme.components.SettingsAction
import io.github.muntasimulhaque.ninetynine.ui.theme.components.nameRowTextInset
import io.github.muntasimulhaque.ninetynine.ui.theme.components.paperTopBarColors
import io.github.muntasimulhaque.ninetynine.util.SearchFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NamesViewModel,
    onNameClick: (Int) -> Unit,
    onSettings: () -> Unit,
) {
    val names by viewModel.names.collectAsStateWithLifecycle()
    val namesLoaded by viewModel.namesLoaded.collectAsStateWithLifecycle()
    val learned by viewModel.learned.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    var searching by rememberSaveable { mutableStateOf(false) }
    var dailyNumber by remember { mutableIntStateOf(viewModel.dailyNameNumber()) }

    // The daily name rolls over at midnight; recompute whenever the app resumes.
    LifecycleResumeEffect(Unit) {
        dailyNumber = viewModel.dailyNameNumber()
        onPauseOrDispose {}
    }

    if (searching) {
        BackHandler {
            viewModel.setSearchQuery("")
            searching = false
        }
    }

    val filtered = remember(names, query) { SearchFilter.filter(names, query) }
    val dailyName = remember(names, dailyNumber) { names.firstOrNull { it.number == dailyNumber } }

    // The bar tucks itself away while reading and returns on the first upward pull.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = paperTopBarColors(),
                title = {
                    if (searching) {
                        val focusRequester = remember { FocusRequester() }
                        BasicTextField(
                            value = query,
                            onValueChange = viewModel::setSearchQuery,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            singleLine = true,
                            decorationBox = { inner ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (query.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.search_hint),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    inner()
                                }
                            },
                        )
                        LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    } else {
                        HomeTitle()
                    }
                },
                navigationIcon = {
                    if (searching) {
                        IconButton(onClick = {
                            viewModel.setSearchQuery("")
                            searching = false
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_close_search),
                            )
                        }
                    }
                },
                actions = {
                    if (searching) {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.cd_close_search),
                                )
                            }
                        }
                    } else {
                        IconButton(onClick = { searching = true }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = stringResource(R.string.cd_search),
                            )
                        }
                        // Last in the bar, as on every tab screen. Absent while
                        // searching, which is a mode, not a place.
                        SettingsAction(onSettings)
                    }
                },
            )
        },
    ) { padding ->
        val contentPadding = PaddingValues(
            start = 0.dp,
            end = 0.dp,
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + 16.dp,
        )
        // The rule between rows starts where the names do, not under their numbers.
        val dividerInset = nameRowTextInset()
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (query.isBlank() && dailyName != null) {
                item {
                    DailyHeroCard(dailyName!!, onClick = { onNameClick(dailyName!!.number) })
                }
            }
            if (names.isEmpty() && namesLoaded) {
                // The asset failed to read. Without this the screen would be
                // blank paper with no explanation at all.
                item { PageMessage(stringResource(R.string.names_unavailable)) }
            } else if (filtered.isEmpty() && names.isNotEmpty()) {
                item { PageMessage(stringResource(R.string.no_results)) }
            }
            items(filtered, key = { it.number }) { name ->
                NameListItem(
                    name = name,
                    learned = name.number in learned,
                    onClick = { onNameClick(name.number) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = dividerInset, end = NameRowInset),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

/**
 * The app's full name, set to fit the bar on one line.
 *
 * It must never ellipsize: "The 99 Names of A…" would cut Allah's name, which
 * is the whole reason the launcher label is the short form instead. The bar's
 * height is fixed, so wrapping would clip it too — it shrinks to fit.
 *
 * Material measures a top bar's title with the width left over after the
 * navigation icon and the actions, so the two buttons on the right are already
 * accounted for. The floor is 0.27 rather than the usual 0.55 because the worst
 * case is real: "The Ninety Nine Names of Allah" is 14.864 em in Spectral
 * SemiBold, so on a 320dp screen with the in-app slider at 1.4x on top of a 2.0
 * system font scale it needs 791dp of the 220dp available — a scale of 0.278.
 *
 * That 220dp is the 268dp this bar had with one action, less the 48dp the
 * settings gear takes; the floor moved 0.32 -> 0.27 when the gear arrived. At
 * ordinary sizes almost nothing moves: the name is 282dp of the 343dp bar on a
 * Pixel 4, so it never shrinks there, and a 360dp phone sets it at 0.92.
 */
@Composable
private fun HomeTitle() {
    FitText(
        text = stringResource(R.string.app_title),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        minScale = 0.27f,
    )
}

@Composable
private fun DailyHeroCard(name: Name, onClick: () -> Unit) {
    // The card yields slightly under the finger — paper, not glass.
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = Motion.soft(),
        label = "heroPress",
    )
    Card(
        onClick = onClick,
        interactionSource = interaction,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = HeroContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.notification_title).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = HeroGold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            ArabicText(
                text = name.arabic,
                fontSize = ArabicSize.Panel,
                color = HeroGold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            // The name is a proper noun and gets set whole. Left to wrap, a
            // long one at a large font scale breaks mid-word — "Al-Wa / asi'"
            // — which is the one thing the app is careful never to do.
            FitText(
                text = name.transliteration,
                style = MaterialTheme.typography.displaySmall.copy(
                    textAlign = TextAlign.Center,
                ),
                color = HeroText,
                minScale = 0.45f,
            )
            Spacer(Modifier.height(2.dp))
            // Two lines, and the same slot the share card uses for the same
            // string. On one line this cut the meaning of the day in half —
            // several of the 99 epithets do not fit a phone at default size,
            // so roughly one morning in eight the app opened on "The Perfect
            // Lord And Master Upon Whom Th…". The card has the height to spare.
            Text(
                text = name.title,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                color = HeroSubtext,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

