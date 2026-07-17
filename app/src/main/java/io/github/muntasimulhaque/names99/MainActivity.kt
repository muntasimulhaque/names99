package io.github.muntasimulhaque.names99

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.muntasimulhaque.names99.daily.DailyScheduler
import io.github.muntasimulhaque.names99.data.Prefs
import io.github.muntasimulhaque.names99.data.ThemeMode
import io.github.muntasimulhaque.names99.ui.screens.AboutScreen
import io.github.muntasimulhaque.names99.ui.screens.DetailScreen
import io.github.muntasimulhaque.names99.ui.screens.FlashcardsScreen
import io.github.muntasimulhaque.names99.ui.screens.HomeScreen
import io.github.muntasimulhaque.names99.ui.screens.MemorizeScreen
import io.github.muntasimulhaque.names99.ui.screens.QuizScreen
import io.github.muntasimulhaque.names99.ui.screens.SettingsScreen
import io.github.muntasimulhaque.names99.ui.theme.Names99Theme

class MainActivity : ComponentActivity() {

    private val startNumber = mutableStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DailyScheduler.ensureScheduled(applicationContext)
        startNumber.value = intent.getIntExtra(EXTRA_NAME_NUMBER, -1)
        setContent { App(startNumber) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        startNumber.value = intent.getIntExtra(EXTRA_NAME_NUMBER, -1)
    }

    companion object {
        const val EXTRA_NAME_NUMBER = "nameNumber"
    }
}

@Composable
private fun App(startNumber: MutableState<Int>) {
    val context = LocalContext.current
    val prefs = remember { Prefs(context.applicationContext) }
    val themeMode by prefs.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

    Names99Theme(mode = themeMode) {
        val navController = rememberNavController()

        LaunchedEffect(startNumber.value) {
            val number = startNumber.value
            if (number in 1..99) {
                navController.navigate("detail/$number")
                startNumber.value = -1
            }
        }

        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(navController, prefs) }
            composable(
                "detail/{number}",
                arguments = listOf(navArgument("number") { type = NavType.IntType })
            ) { backStackEntry ->
                DetailScreen(
                    navController = navController,
                    prefs = prefs,
                    number = backStackEntry.arguments?.getInt("number") ?: 1
                )
            }
            composable("memorize") { MemorizeScreen(navController, prefs) }
            composable("flashcards") { FlashcardsScreen(navController, prefs) }
            composable("quiz") { QuizScreen(navController) }
            composable("about") { AboutScreen(navController, prefs) }
            composable("settings") { SettingsScreen(navController, prefs) }
        }
    }
}
