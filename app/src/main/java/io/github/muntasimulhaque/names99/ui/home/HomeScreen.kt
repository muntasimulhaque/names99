package io.github.muntasimulhaque.names99.ui.home

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
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.theme.HeroContainer
import io.github.muntasimulhaque.names99.ui.theme.HeroGold
import io.github.muntasimulhaque.names99.ui.theme.HeroSubtext
import io.github.muntasimulhaque.names99.ui.theme.HeroText
import io.github.muntasimulhaque.names99.ui.theme.Motion
import io.github.muntasimulhaque.names99.ui.theme.components.ArabicText
import io.github.muntasimulhaque.names99.ui.theme.components.FitText
import io.github.muntasimulhaque.names99.ui.theme.components.NameListItem
import io.github.muntasimulhaque.names99.ui.theme.components.NameRowInset
import io.github.muntasimulhaque.names99.ui.theme.components.nameRowTextInset
import io.github.muntasimulhaque.names99.ui.theme.components.paperTopBarColors
import io.github.muntasimulhaque.names99.util.SearchFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NamesViewModel,
    onNameClick: (Int) -> Unit,
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
 * It must never ellipsize: "99 Names of A…" would cut Allah's name, which is
 * the whole reason the launcher label is the short form. At the largest text
 * sizes (the in-app slider at 1.4x on top of a large system font scale) the
 * full name is wider than the bar, and the bar's height is fixed, so wrapping
 * would clip it. At ordinary sizes nothing moves — the name is less than half
 * the bar's width.
 */
@Composable
private fun HomeTitle() {
    FitText(
        text = stringResource(R.string.app_title),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
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
                fontSize = 40.sp,
                color = HeroGold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = name.transliteration,
                style = MaterialTheme.typography.displaySmall,
                color = HeroText,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = name.title,
                style = MaterialTheme.typography.titleSmall,
                fontStyle = FontStyle.Italic,
                color = HeroSubtext,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** A quiet line of italic explanation where the list would have been. */
@Composable
private fun PageMessage(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
