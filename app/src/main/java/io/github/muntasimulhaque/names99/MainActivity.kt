package io.github.muntasimulhaque.names99

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.about.AboutScreen
import io.github.muntasimulhaque.names99.ui.detail.DetailScreen
import io.github.muntasimulhaque.names99.ui.home.HomeScreen
import io.github.muntasimulhaque.names99.ui.memorize.FlashcardsScreen
import io.github.muntasimulhaque.names99.ui.memorize.MemorizeScreen
import io.github.muntasimulhaque.names99.ui.memorize.QuizScreen
import io.github.muntasimulhaque.names99.ui.settings.SettingsScreen
import io.github.muntasimulhaque.names99.ui.theme.Motion
import io.github.muntasimulhaque.names99.ui.theme.Names99Theme

class MainActivity : ComponentActivity() {

    private var startNumber by mutableIntStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startNumber = consumeNameNumber(intent)
        setContent { App(startNumber, onStartNumberConsumed = { startNumber = -1 }) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        startNumber = consumeNameNumber(intent)
    }

    /** Reads the extra, then removes it so a configuration change can't replay the navigation. */
    private fun consumeNameNumber(intent: Intent?): Int {
        val number = intent?.getIntExtra(EXTRA_NAME_NUMBER, -1) ?: -1
        intent?.removeExtra(EXTRA_NAME_NUMBER)
        return number
    }

    companion object {
        const val EXTRA_NAME_NUMBER = "nameNumber"
    }
}

private data class TopLevelRoute(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
)

private val topLevelRoutes = listOf(
    TopLevelRoute("names", R.string.nav_names, Icons.AutoMirrored.Filled.MenuBook),
    TopLevelRoute("memorize", R.string.memorize, Icons.Filled.School),
    TopLevelRoute("settings", R.string.settings, Icons.Filled.Settings),
)

@Composable
private fun App(startNumber: Int, onStartNumberConsumed: () -> Unit) {
    val viewModel: NamesViewModel = viewModel()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val textScale by viewModel.textScale.collectAsStateWithLifecycle()

    Names99Theme(themeMode = themeMode, textScale = textScale) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val showBottomBar = currentRoute in topLevelRoutes.map { it.route }

        LaunchedEffect(startNumber) {
            if (startNumber in 1..99) {
                onStartNumberConsumed()
                navController.navigate("detail/$startNumber")
            }
        }

        Column(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "names",
                modifier = Modifier.weight(1f),
                // Pushed screens rise gently into place; pops sink away.
                enterTransition = {
                    fadeIn(tween(Motion.GENTLE, easing = Motion.Settle)) +
                        slideInVertically(tween(Motion.GENTLE, easing = Motion.Settle)) { it / 24 }
                },
                exitTransition = { fadeOut(tween(Motion.QUICK)) },
                popEnterTransition = { fadeIn(tween(Motion.GENTLE)) },
                popExitTransition = {
                    fadeOut(tween(Motion.GENTLE)) +
                        slideOutVertically(tween(Motion.GENTLE, easing = Motion.Settle)) { it / 24 }
                },
            ) {
                composable("names", enterTransition = tabFade, exitTransition = tabFadeOut) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNameClick = { number -> navController.navigate("detail/$number") },
                    )
                }
                composable(
                    "detail/{number}",
                    arguments = listOf(navArgument("number") { type = NavType.IntType }),
                ) { entry ->
                    DetailScreen(
                        viewModel = viewModel,
                        startNumber = entry.arguments?.getInt("number") ?: 1,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable("memorize", enterTransition = tabFade, exitTransition = tabFadeOut) {
                    MemorizeScreen(
                        viewModel = viewModel,
                        onFlashcards = { navController.navigate("flashcards") },
                        onQuiz = { navController.navigate("quiz") },
                    )
                }
                composable("flashcards") {
                    FlashcardsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable("quiz") {
                    QuizScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable("settings", enterTransition = tabFade, exitTransition = tabFadeOut) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onAbout = { navController.navigate("about") },
                    )
                }
                composable("about") {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
            }
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(tween(Motion.QUICK)) + slideInVertically(tween(Motion.GENTLE)) { it },
                exit = fadeOut(tween(Motion.QUICK)) + slideOutVertically(tween(Motion.GENTLE)) { it },
            ) {
                QuietBottomBar(navController, currentRoute)
            }
        }
    }
}

/** Tab switches crossfade — only pushed detail screens use the rising motion. */
private val tabFade:
    (androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() ->
    androidx.compose.animation.EnterTransition?) = {
    fadeIn(tween(Motion.GENTLE))
}

private val tabFadeOut:
    (androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() ->
    androidx.compose.animation.ExitTransition?) = {
    fadeOut(tween(Motion.QUICK))
}

/**
 * A bespoke, quiet bottom bar: no pill indicator, no tonal blocks — just a
 * hairline rule and three small-caps labels, selection carried by color.
 */
@Composable
private fun QuietBottomBar(navController: NavHostController, currentRoute: String?) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(60.dp)
                    .selectableGroup(),
            ) {
                topLevelRoutes.forEach { item ->
                    val selected = currentRoute == item.route
                    val tint by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(Motion.QUICK),
                        label = "tabTint",
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .selectable(
                                selected = selected,
                                role = Role.Tab,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.height(22.dp),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(item.labelRes).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = tint,
                        )
                    }
                }
            }
        }
    }
}
