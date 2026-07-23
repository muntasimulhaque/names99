package io.github.muntasimulhaque.names99

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
import io.github.muntasimulhaque.names99.data.ThemeMode
import io.github.muntasimulhaque.names99.ui.NamesViewModel
import io.github.muntasimulhaque.names99.ui.about.AboutScreen
import io.github.muntasimulhaque.names99.ui.detail.DetailScreen
import io.github.muntasimulhaque.names99.ui.home.HomeScreen
import io.github.muntasimulhaque.names99.ui.memorize.FlashcardsScreen
import io.github.muntasimulhaque.names99.ui.memorize.MemorizeScreen
import io.github.muntasimulhaque.names99.ui.memorize.QuizScreen
import io.github.muntasimulhaque.names99.ui.settings.SettingsScreen
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
            ) {
                composable("names") {
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
                composable("memorize") {
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
                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        onAbout = { navController.navigate("about") },
                    )
                }
                composable("about") {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
            }
            if (showBottomBar) {
                NamesBottomBar(navController, currentRoute)
            }
        }
    }
}

@Composable
private fun NamesBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        topLevelRoutes.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelRes)) },
            )
        }
    }
}
