package io.github.muntasimulhaque.names99.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.NamesRepository
import io.github.muntasimulhaque.names99.data.Prefs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, prefs: Prefs, number: Int) {
    val context = LocalContext.current
    val name = remember(number) { NamesRepository.byNumber(context, number) } ?: return
    val learned by prefs.learned.collectAsState(initial = emptySet())
    val textScale by prefs.textScale.collectAsState(initial = 1f)
    val scope = rememberCoroutineScope()
    val isLearned = number in learned

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${name.number} / 99") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        navController.navigate("detail/${number - 1}") {
                            popUpTo("home")
                        }
                    },
                    enabled = number > 1
                ) {
                    Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null)
                    Text(NamesRepository.byNumber(context, number - 1)?.transliteration ?: "")
                }
                TextButton(
                    onClick = {
                        navController.navigate("detail/${number + 1}") {
                            popUpTo("home")
                        }
                    },
                    enabled = number < 99
                ) {
                    Text(NamesRepository.byNumber(context, number + 1)?.transliteration ?: "")
                    Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = name.arabic,
                fontFamily = FontFamily.Serif,
                fontSize = (56 * textScale).sp,
                lineHeight = (76 * textScale).sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = name.transliteration,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = name.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            FilterChip(
                selected = isLearned,
                onClick = { scope.launch { prefs.setLearned(number, !isLearned) } },
                label = {
                    Text(stringResource(if (isLearned) R.string.learned else R.string.mark_learned))
                },
                leadingIcon = {
                    Icon(
                        if (isLearned) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = null
                    )
                }
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = name.meaning,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = (17 * textScale).sp,
                lineHeight = (28 * textScale).sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            name.note?.let { note ->
                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.note_label).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = (15 * textScale).sp,
                            lineHeight = (24 * textScale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
