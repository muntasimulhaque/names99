package io.github.muntasimulhaque.names99.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.muntasimulhaque.names99.daily.DailyScheduler
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.data.NamesRepository
import io.github.muntasimulhaque.names99.data.Prefs
import io.github.muntasimulhaque.names99.data.ThemeMode
import io.github.muntasimulhaque.names99.data.ViewMode
import io.github.muntasimulhaque.names99.util.DailyName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Activity-scoped state shared by all screens. */
class NamesViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = Prefs(application)

    val names: StateFlow<List<Name>> = flow { emit(NamesRepository.load(application)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val learned: StateFlow<Set<Int>> = prefs.learned
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val themeMode: StateFlow<ThemeMode> = prefs.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val textScale: StateFlow<Float> = prefs.textScale
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1f)

    val viewMode: StateFlow<ViewMode> = prefs.viewMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ViewMode.LIST)

    val quizBest: StateFlow<Int> = prefs.quizBest
        .stateIn(viewModelScope, SharingStarted.Eagerly, -1)

    val includeLearned: StateFlow<Boolean> = prefs.includeLearned
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val dailyEnabled: StateFlow<Boolean> = prefs.dailyEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val dailyTime: StateFlow<Pair<Int, Int>> = prefs.dailyTime
        .stateIn(viewModelScope, SharingStarted.Eagerly, 8 to 0)

    val searchQuery = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun dailyNameNumber(): Int = DailyName.numberFor(System.currentTimeMillis())

    fun setLearned(number: Int, value: Boolean) = viewModelScope.launch {
        prefs.setLearned(number, value)
    }

    fun resetProgress() = viewModelScope.launch {
        prefs.resetLearned()
    }

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch {
        prefs.setThemeMode(mode)
    }

    fun setTextScale(scale: Float) = viewModelScope.launch {
        prefs.setTextScale(scale)
    }

    fun setDailyEnabled(enabled: Boolean) = viewModelScope.launch {
        prefs.setDailyEnabled(enabled)
        if (enabled) DailyScheduler.rescheduleNotification(getApplication())
        else DailyScheduler.cancelNotification(getApplication())
    }

    fun setDailyTime(hour: Int, minute: Int) = viewModelScope.launch {
        prefs.setDailyTime(hour, minute)
        DailyScheduler.rescheduleNotification(getApplication())
    }

    fun setViewMode(mode: ViewMode) = viewModelScope.launch {
        prefs.setViewMode(mode)
    }

    fun setQuizBest(score: Int) = viewModelScope.launch {
        prefs.setQuizBest(score)
    }

    fun setIncludeLearned(include: Boolean) = viewModelScope.launch {
        prefs.setIncludeLearned(include)
    }
}
