package io.github.muntasimulhaque.ninetynine.ui.memorize

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.muntasimulhaque.ninetynine.R
import io.github.muntasimulhaque.ninetynine.ui.NamesViewModel
import io.github.muntasimulhaque.ninetynine.ui.theme.components.BackButton
import io.github.muntasimulhaque.ninetynine.ui.theme.components.NameListItem
import io.github.muntasimulhaque.ninetynine.ui.theme.components.NameRowInset
import io.github.muntasimulhaque.ninetynine.ui.theme.components.PageMessage
import io.github.muntasimulhaque.ninetynine.ui.theme.components.ScreenLabel
import io.github.muntasimulhaque.ninetynine.ui.theme.components.nameRowTextInset
import io.github.muntasimulhaque.ninetynine.ui.theme.components.paperTopBarColors

/**
 * The names the reader has marked learned.
 *
 * Memorize showed the count as a large, beautiful and completely inert number:
 * there was no way anywhere in the app to see *which* names it stood for,
 * short of scrolling all 99 looking for gold ticks. Bookmarks — the newer and
 * lighter of the two axes — had a whole tab. This is the same list in the same
 * rows, so a statistic becomes somewhere to go.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnedScreen(
    viewModel: NamesViewModel,
    onNameClick: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val names by viewModel.names.collectAsStateWithLifecycle()
    val namesLoaded by viewModel.namesLoaded.collectAsStateWithLifecycle()
    val learned by viewModel.learned.collectAsStateWithLifecycle()
    val learnedLoaded by viewModel.learnedLoaded.collectAsStateWithLifecycle()

    // Book order, like every other list in the app.
    val known = remember(names, learned) { names.filter { it.number in learned } }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = paperTopBarColors(),
                title = { ScreenLabel(stringResource(R.string.learned_names)) },
                navigationIcon = { BackButton(onBack) },
            )
        },
    ) { padding ->
        val dividerInset = nameRowTextInset()
        LazyColumn(
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 16.dp,
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            if (known.isEmpty() && learnedLoaded && namesLoaded) {
                item {
                    PageMessage(
                        stringResource(
                            if (namesLoaded && names.isEmpty()) R.string.names_unavailable
                            else R.string.no_learned
                        )
                    )
                }
            }
            items(known, key = { it.number }) { name ->
                NameListItem(
                    name = name,
                    learned = true,
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
