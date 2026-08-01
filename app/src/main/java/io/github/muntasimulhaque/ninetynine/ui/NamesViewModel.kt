package io.github.muntasimulhaque.ninetynine.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.muntasimulhaque.ninetynine.daily.DailyScheduler
import io.github.muntasimulhaque.ninetynine.data.Name
import io.github.muntasimulhaque.ninetynine.data.NamesRepository
import io.github.muntasimulhaque.ninetynine.data.Prefs
import io.github.muntasimulhaque.ninetynine.data.ThemeMode
import io.github.muntasimulhaque.ninetynine.util.DailyName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Activity-scoped state shared by all screens. */
class NamesViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = Prefs(application)

    private val _namesLoaded = MutableStateFlow(false)

    /**
     * False only while the asset is still being read. [names] is empty both
     * before the read and after a failed one; this is what tells the screens
     * which of the two they are looking at.
     */
    val namesLoaded: StateFlow<Boolean> = _namesLoaded.asStateFlow()

    val names: StateFlow<List<Name>> = flow {
        val loaded = NamesRepository.load(application)
        _namesLoaded.value = true   // set before emitting, so the two agree
        emit(loaded)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val learned: StateFlow<Set<Int>> = prefs.learned
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val bookmarked: StateFlow<Set<Int>> = prefs.bookmarked
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val themeMode: StateFlow<ThemeMode> = prefs.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val textScale: StateFlow<Float> = prefs.textScale
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1f)

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

    fun setBookmarked(number: Int, value: Boolean) = viewModelScope.launch {
        prefs.setBookmarked(number, value)
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

    fun setQuizBest(score: Int) = viewModelScope.launch {
        prefs.setQuizBest(score)
    }

    fun setIncludeLearned(include: Boolean) = viewModelScope.launch {
        prefs.setIncludeLearned(include)
    }
}
