package io.github.muntasimulhaque.names99.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.data.NamesRepository

private data class Question(val name: Name, val options: List<String>)

/**
 * Each question is encoded as [numberString, option1..option4] so the quiz can be
 * kept in rememberSaveable (survives rotation). distinct() guards against any
 * future locale where two names share a title.
 */
private fun buildQuiz(all: List<Name>, count: Int = 10): ArrayList<ArrayList<String>> =
    ArrayList(
        all.shuffled().take(count).map { name ->
            val distractors = all.map { it.title }.distinct()
                .filter { it != name.title }
                .shuffled().take(3)
            ArrayList(listOf(name.number.toString()) + (distractors + name.title).shuffled())
        }
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController) {
    val context = LocalContext.current
    val all = remember { NamesRepository.load(context) }
    var quizData by rememberSaveable { mutableStateOf(buildQuiz(all)) }
    val quiz = remember(quizData) {
        quizData.mapNotNull { q ->
            NamesRepository.byNumber(context, q[0].toInt())?.let { Question(it, q.drop(1)) }
        }
    }
    var index by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableStateOf("") } // "" = nothing selected yet
    var finished by rememberSaveable { mutableStateOf(false) }

    fun restart() {
        quizData = buildQuiz(all); index = 0; score = 0; selected = ""; finished = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quiz)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (finished) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "$score / ${quiz.size}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(
                        when {
                            score == quiz.size -> R.string.quiz_perfect
                            score >= quiz.size / 2 -> R.string.quiz_good
                            else -> R.string.quiz_keep_trying
                        }
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = { restart() }) {
                    Icon(Icons.Default.Replay, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.try_another_round))
                }
            }
            return@Scaffold
        }

        val q = quiz[index]
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (index + 1) / quiz.size.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.question_x_of_y, index + 1, quiz.size),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = q.name.arabic,
                fontFamily = FontFamily.Serif,
                fontSize = 44.sp,
                lineHeight = 60.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(q.name.transliteration, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))

            q.options.forEach { option ->
                val isCorrect = option == q.name.title
                val revealed = selected.isNotEmpty()
                val colors = when {
                    revealed && isCorrect -> MaterialTheme.colorScheme.primary
                    revealed && option == selected -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }
                OutlinedButton(
                    onClick = {
                        if (selected.isEmpty()) {
                            selected = option
                            if (isCorrect) score++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    border = BorderStroke(
                        width = if (revealed && (isCorrect || option == selected)) 2.dp else 1.dp,
                        color = colors
                    )
                ) {
                    Text(
                        option,
                        color = if (revealed && (isCorrect || option == selected)) colors
                        else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (index + 1 >= quiz.size) finished = true
                    else { index++; selected = "" }
                },
                enabled = selected.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(stringResource(if (index + 1 >= quiz.size) R.string.see_result else R.string.next))
            }
        }
    }
}
