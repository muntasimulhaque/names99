package io.github.muntasimulhaque.names99.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.data.ViewMode
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.theme.HeroContainer
import io.github.muntasimulhaque.names99.ui.theme.HeroGold
import io.github.muntasimulhaque.names99.ui.theme.HeroSubtext
import io.github.muntasimulhaque.names99.ui.theme.HeroText
import io.github.muntasimulhaque.names99.ui.theme.components.ArabicText
import io.github.muntasimulhaque.names99.ui.theme.components.NameListItem
import io.github.muntasimulhaque.names99.util.SearchFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NamesViewModel,
    onNameClick: (Int) -> Unit,
) {
    val names by viewModel.names.collectAsStateWithLifecycle()
    val learned by viewModel.learned.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    var searching by rememberSaveable { mutableStateOf(false) }
    var dailyNumber by remember { mutableIntStateOf(viewModel.dailyNameNumber()) }

    // The daily name rolls over at midnight; recompute whenever the app resumes.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) dailyNumber = viewModel.dailyNameNumber()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (searching) {
        BackHandler {
            viewModel.setSearchQuery("")
            searching = false
        }
    }

    val filtered = remember(names, query) { SearchFilter.filter(names, query) }
    val dailyName = remember(names, dailyNumber) { names.firstOrNull { it.number == dailyNumber } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (searching) {
                        val focusRequester = remember { FocusRequester() }
                        OutlinedTextField(
                            value = query,
                            onValueChange = viewModel::setSearchQuery,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            placeholder = { Text(stringResource(R.string.search_hint)) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.large,
                        )
                        LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    } else {
                        Text(
                            text = stringResource(R.string.launcher_name),
                            style = MaterialTheme.typography.headlineSmall,
                        )
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
                        IconButton(onClick = {
                            viewModel.setViewMode(
                                if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                            )
                        }) {
                            Icon(
                                if (viewMode == ViewMode.LIST) Icons.Filled.GridView
                                else Icons.AutoMirrored.Filled.ViewList,
                                contentDescription = stringResource(
                                    if (viewMode == ViewMode.LIST) R.string.cd_view_grid
                                    else R.string.cd_view_list
                                ),
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
        if (viewMode == ViewMode.GRID) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            ) {
                if (query.isBlank() && dailyName != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        DailyHeroCard(dailyName!!, onClick = { onNameClick(dailyName!!.number) })
                    }
                }
                if (filtered.isEmpty() && names.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { NoResults() }
                }
                gridItems(filtered, key = { it.number }) { name ->
                    NameGridCell(
                        name = name,
                        learned = name.number in learned,
                        onClick = { onNameClick(name.number) },
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
            ) {
                if (query.isBlank() && dailyName != null) {
                    item {
                        DailyHeroCard(dailyName!!, onClick = { onNameClick(dailyName!!.number) })
                    }
                }
                if (filtered.isEmpty() && names.isNotEmpty()) {
                    item { NoResults() }
                }
                items(filtered, key = { it.number }) { name ->
                    NameListItem(
                        name = name,
                        learned = name.number in learned,
                        onClick = { onNameClick(name.number) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 66.dp, end = 20.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyHeroCard(name: Name, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
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
                fontSize = 42.sp,
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

@Composable
private fun NameGridCell(
    name: Name,
    learned: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(0.85f),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = name.number.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                ArabicText(
                    text = name.arabic,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (learned) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun NoResults() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_results),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
