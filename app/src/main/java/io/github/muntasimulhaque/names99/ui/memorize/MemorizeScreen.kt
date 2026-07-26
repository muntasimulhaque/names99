package io.github.muntasimulhaque.names99.ui.memorize

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.theme.components.HairlineProgress
import io.github.muntasimulhaque.names99.ui.theme.components.NavRow
import io.github.muntasimulhaque.names99.ui.theme.components.PageRule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorizeScreen(
    viewModel: NamesViewModel,
    onFlashcards: () -> Unit,
    onQuiz: () -> Unit,
) {
    val learned by viewModel.learned.collectAsStateWithLifecycle()
    val quizBest by viewModel.quizBest.collectAsStateWithLifecycle()
    val learnedCount = learned.size.coerceIn(0, 99)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.memorize),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(20.dp))
            // Progress as typography: a big light number, a quiet caption,
            // and a hairline of gold — no rings, no dashboards.
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = learnedCount.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.progress_of_caption),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 9.dp),
                )
            }
            Spacer(Modifier.height(14.dp))
            HairlineProgress(progress = learnedCount / 99f)
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (learnedCount >= 99)
                    stringResource(R.string.all_learned_title)
                else
                    stringResource(R.string.remaining_count, 99 - learnedCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(36.dp))
            // A small table of contents, set like a book's.
            NavRow(
                title = stringResource(R.string.flashcards),
                subtitle = stringResource(R.string.flashcards_subtitle),
                onClick = onFlashcards,
            )
            PageRule()
            NavRow(
                title = stringResource(R.string.quiz),
                subtitle = stringResource(R.string.quiz_subtitle),
                onClick = onQuiz,
            )
            PageRule()
            if (quizBest >= 0) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.quiz_best, quizBest),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

