package io.github.muntasimulhaque.names99.data

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore(
    name = "settings",
    // A half-written file (interrupted write, bad shutdown, filesystem damage)
    // would otherwise throw on every read forever. Starting over from defaults
    // loses the stored values, but the alternative is an app that cannot launch.
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

enum class ThemeMode { SYSTEM, LIGHT, DARK, BLACK }

class Prefs(private val context: Context) {

    /**
     * Every read goes through here.
     *
     * DataStore's `data` flow throws [IOException] when the file cannot be
     * read. These flows are collected in `stateIn(viewModelScope, …)`, which
     * has no exception handler, so an uncaught throw reaches the thread's
     * default handler and kills the process — on launch, every launch, with
     * no way out but clearing app data. That would destroy the one thing this
     * app stores: which of the 99 names the reader has learned.
     */
    private val data: Flow<Preferences> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }

    private object Keys {
        val LEARNED = stringSetPreferencesKey("learned")
        val THEME = stringPreferencesKey("theme")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val DAILY_ENABLED = booleanPreferencesKey("daily_enabled")
        val DAILY_HOUR = intPreferencesKey("daily_hour")
        val DAILY_MINUTE = intPreferencesKey("daily_minute")
        val QUIZ_BEST = intPreferencesKey("quiz_best")
        val INCLUDE_LEARNED = booleanPreferencesKey("include_learned")
    }

    val learned: Flow<Set<Int>> = data
        .map { p -> p[Keys.LEARNED]?.mapNotNull(String::toIntOrNull)?.toSet() ?: emptySet() }

    val themeMode: Flow<ThemeMode> = data
        .map { p -> runCatching { ThemeMode.valueOf(p[Keys.THEME] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM) }

    val textScale: Flow<Float> = data
        .map { p -> p[Keys.TEXT_SCALE] ?: 1f }

    val dailyEnabled: Flow<Boolean> = data
        .map { p -> p[Keys.DAILY_ENABLED] ?: false }

    val dailyTime: Flow<Pair<Int, Int>> = data
        .map { p -> (p[Keys.DAILY_HOUR] ?: 8) to (p[Keys.DAILY_MINUTE] ?: 0) }

    /** Best quiz score so far, or -1 when no round has been finished. */
    val quizBest: Flow<Int> = data
        .map { p -> p[Keys.QUIZ_BEST] ?: -1 }

    val includeLearned: Flow<Boolean> = data
        .map { p -> p[Keys.INCLUDE_LEARNED] ?: false }

    /**
     * Writes fail the same way reads do, and from a `viewModelScope.launch`
     * they crash just as hard. A setting that failed to save is not worth the
     * process; the value simply stays as it was.
     */
    private suspend fun write(block: (MutablePreferences) -> Unit) {
        try {
            context.dataStore.edit(block)
        } catch (_: IOException) {
        }
    }

    suspend fun setLearned(number: Int, value: Boolean) = write { p ->
        val current = p[Keys.LEARNED]?.toMutableSet() ?: mutableSetOf()
        if (value) current.add(number.toString()) else current.remove(number.toString())
        p[Keys.LEARNED] = current
    }

    suspend fun resetLearned() = write {
        it[Keys.LEARNED] = emptySet()
        it.remove(Keys.QUIZ_BEST)
    }

    suspend fun setThemeMode(mode: ThemeMode) = write { it[Keys.THEME] = mode.name }

    suspend fun setTextScale(scale: Float) = write { it[Keys.TEXT_SCALE] = scale }

    suspend fun setDailyEnabled(enabled: Boolean) = write { it[Keys.DAILY_ENABLED] = enabled }

    suspend fun setDailyTime(hour: Int, minute: Int) = write {
        it[Keys.DAILY_HOUR] = hour
        it[Keys.DAILY_MINUTE] = minute
    }

    /** Keeps the running maximum; lower scores are ignored. */
    suspend fun setQuizBest(score: Int) = write {
        if (score > (it[Keys.QUIZ_BEST] ?: -1)) it[Keys.QUIZ_BEST] = score
    }

    suspend fun setIncludeLearned(include: Boolean) = write { it[Keys.INCLUDE_LEARNED] = include }
}
