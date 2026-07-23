package io.github.muntasimulhaque.names99.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK, BLACK }

enum class ViewMode { LIST, GRID }

class Prefs(private val context: Context) {

    private object Keys {
        val LEARNED = stringSetPreferencesKey("learned")
        val THEME = stringPreferencesKey("theme")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val DAILY_ENABLED = booleanPreferencesKey("daily_enabled")
        val DAILY_HOUR = intPreferencesKey("daily_hour")
        val DAILY_MINUTE = intPreferencesKey("daily_minute")
        val VIEW_MODE = stringPreferencesKey("view_mode")
        val QUIZ_BEST = intPreferencesKey("quiz_best")
        val INCLUDE_LEARNED = booleanPreferencesKey("include_learned")
    }

    val learned: Flow<Set<Int>> = context.dataStore.data
        .map { p -> p[Keys.LEARNED]?.mapNotNull(String::toIntOrNull)?.toSet() ?: emptySet() }

    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { p -> runCatching { ThemeMode.valueOf(p[Keys.THEME] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM) }

    val textScale: Flow<Float> = context.dataStore.data
        .map { p -> p[Keys.TEXT_SCALE] ?: 1f }

    val dailyEnabled: Flow<Boolean> = context.dataStore.data
        .map { p -> p[Keys.DAILY_ENABLED] ?: false }

    val dailyTime: Flow<Pair<Int, Int>> = context.dataStore.data
        .map { p -> (p[Keys.DAILY_HOUR] ?: 8) to (p[Keys.DAILY_MINUTE] ?: 0) }

    val viewMode: Flow<ViewMode> = context.dataStore.data
        .map { p -> runCatching { ViewMode.valueOf(p[Keys.VIEW_MODE] ?: "LIST") }.getOrDefault(ViewMode.LIST) }

    /** Best quiz score so far, or -1 when no round has been finished. */
    val quizBest: Flow<Int> = context.dataStore.data
        .map { p -> p[Keys.QUIZ_BEST] ?: -1 }

    val includeLearned: Flow<Boolean> = context.dataStore.data
        .map { p -> p[Keys.INCLUDE_LEARNED] ?: false }

    suspend fun setLearned(number: Int, value: Boolean) {
        context.dataStore.edit { p ->
            val current = p[Keys.LEARNED]?.toMutableSet() ?: mutableSetOf()
            if (value) current.add(number.toString()) else current.remove(number.toString())
            p[Keys.LEARNED] = current
        }
    }

    suspend fun resetLearned() = context.dataStore.edit {
        it[Keys.LEARNED] = emptySet()
        it.remove(Keys.QUIZ_BEST)
    }

    suspend fun setThemeMode(mode: ThemeMode) = context.dataStore.edit { it[Keys.THEME] = mode.name }

    suspend fun setTextScale(scale: Float) = context.dataStore.edit { it[Keys.TEXT_SCALE] = scale }

    suspend fun setDailyEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.DAILY_ENABLED] = enabled }

    suspend fun setDailyTime(hour: Int, minute: Int) = context.dataStore.edit {
        it[Keys.DAILY_HOUR] = hour
        it[Keys.DAILY_MINUTE] = minute
    }

    suspend fun setViewMode(mode: ViewMode) = context.dataStore.edit { it[Keys.VIEW_MODE] = mode.name }

    /** Keeps the running maximum; lower scores are ignored. */
    suspend fun setQuizBest(score: Int) = context.dataStore.edit {
        if (score > (it[Keys.QUIZ_BEST] ?: -1)) it[Keys.QUIZ_BEST] = score
    }

    suspend fun setIncludeLearned(include: Boolean) =
        context.dataStore.edit { it[Keys.INCLUDE_LEARNED] = include }
}
