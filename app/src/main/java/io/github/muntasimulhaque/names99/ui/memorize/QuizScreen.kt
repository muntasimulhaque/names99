package io.github.muntasimulhaque.names99.ui.memorize

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.theme.components.ArabicText
import io.github.muntasimulhaque.names99.util.QuizBuilder
import io.github.muntasimulhaque.names99.util.QuizQuestion

/** Session state for one quiz round; survives rotation with the ViewModel. */
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    var questions by mutableStateOf<List<QuizQuestion>>(emptyList()); private set
    var index by mutableIntStateOf(0); private set
    var score by mutableIntStateOf(0); private set
    var selected by mutableIntStateOf(-1); private set
    var finished by mutableStateOf(false); private set

    fun ensureQuiz(names: List<Name>) {
        if (questions.isEmpty() && names.isNotEmpty()) {
            questions = QuizBuilder.build(names)
        }
    }

    fun select(optionIndex: Int) {
        if (selected != -1) return
        selected = optionIndex
        if (optionIndex == questions[index].answerIndex) score++
    }

    fun next() {
        if (index < questions.lastIndex) {
            index++
            selected = -1
        } else {
            finished = true
        }
    }

    fun restart(names: List<Name>) {
        questions = QuizBuilder.build(names)
        index = 0
        score = 0
        selected = -1
        finished = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: NamesViewModel,
    onBack: () -> Unit,
) {
    val quiz: QuizViewModel = viewModel()
    val names by viewModel.names.collectAsStateWithLifecycle()
    val quizBest by viewModel.quizBest.collectAsStateWithLifecycle()

    LaunchedEffect(names) { quiz.ensureQuiz(names) }
    LaunchedEffect(quiz.finished) {
        if (quiz.finished) viewModel.setQuizBest(quiz.score)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!quiz.finished && quiz.questions.isNotEmpty()) {
                        Text(
                            stringResource(
                                R.string.question_x_of_y,
                                quiz.index + 1,
                                quiz.questions.size,
                            )
                        )
                    } else {
                        Text(stringResource(R.string.quiz))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
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
                quiz.questions.isEmpty() -> Unit
                quiz.finished -> QuizResultContent(
                    score = quiz.score,
                    total = quiz.questions.size,
                    best = quizBest,
                    onRestart = { quiz.restart(names) },
                    onBack = onBack,
                )
                else -> QuizQuestionContent(
                    quiz = quiz,
                    names = names,
                )
            }
        }
    }
}

@Composable
private fun QuizQuestionContent(
    quiz: QuizViewModel,
    names: List<Name>,
) {
    val question = quiz.questions[quiz.index]
    val name = names.firstOrNull { it.number == question.number } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ArabicText(
                    text = name.arabic,
                    fontSize = 44.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        question.options.forEachIndexed { optionIndex, option ->
            OptionButton(
                text = option,
                state = when {
                    quiz.selected == -1 -> OptionState.IDLE
                    optionIndex == question.answerIndex -> OptionState.CORRECT
                    optionIndex == quiz.selected -> OptionState.WRONG
                    else -> OptionState.DIMMED
                },
                enabled = quiz.selected == -1,
                onClick = { quiz.select(optionIndex) },
            )
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = quiz::next,
            enabled = quiz.selected != -1,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                stringResource(
                    if (quiz.index == quiz.questions.lastIndex) R.string.see_result
                    else R.string.next
                )
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

private enum class OptionState { IDLE, CORRECT, WRONG, DIMMED }

@Composable
private fun OptionButton(
    text: String,
    state: OptionState,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val (container, content, border) = when (state) {
        OptionState.IDLE -> Triple(colors.surface, colors.onSurface, colors.outline)
        OptionState.CORRECT -> Triple(colors.primaryContainer, colors.onPrimaryContainer, colors.primary)
        OptionState.WRONG -> Triple(colors.errorContainer, colors.onErrorContainer, colors.error)
        OptionState.DIMMED -> Triple(
            colors.surface,
            colors.onSurface.copy(alpha = 0.45f),
            colors.outline.copy(alpha = 0.45f),
        )
    }
    val stateCd = when (state) {
        OptionState.CORRECT -> stringResource(R.string.cd_correct)
        OptionState.WRONG -> stringResource(R.string.cd_wrong)
        else -> null
    }
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (stateCd != null) Modifier.semantics { contentDescription = stateCd }
                else Modifier
            ),
        shape = MaterialTheme.shapes.medium,
        color = container,
        border = BorderStroke(if (state == OptionState.IDLE) 1.dp else 2.dp, border),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = content,
                modifier = Modifier.weight(1f),
            )
            when (state) {
                OptionState.CORRECT -> Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = colors.primary,
                )
                OptionState.WRONG -> Icon(
                    Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = colors.error,
                )
                else -> Unit
            }
        }
    }
}

@Composable
private fun QuizResultContent(
    score: Int,
    total: Int,
    best: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.quiz_score_format, score, total),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(
                when {
                    score == total -> R.string.quiz_perfect
                    score >= total / 2 -> R.string.quiz_good
                    else -> R.string.quiz_keep_trying
                }
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        if (best >= 0) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.quiz_best, maxOf(best, score)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(28.dp))
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.try_another_round))
        }
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.cd_back))
        }
    }
}
