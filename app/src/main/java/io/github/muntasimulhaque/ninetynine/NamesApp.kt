package io.github.muntasimulhaque.ninetynine

import android.app.Application
import io.github.muntasimulhaque.ninetynine.daily.DailyScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NamesApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Channel exists from first app start, so it is visible in system settings right away.
        DailyScheduler.createNotificationChannel(this)

        // KEEP, never re-anchor. Application.onCreate runs on EVERY process
        // start — including the one WorkManager itself begins in order to run a
        // worker, because Application.onCreate completes before onStartJob. A
        // re-anchor here therefore cancelled the very work that woke the
        // process and pushed it a day out, so on a cold start the widget kept
        // showing yesterday's name and the daily notification never arrived.
        // That is silent, intermittent, and hits exactly the reader these
        // features exist for: the one who takes the name off the home screen
        // and rarely opens the app.
        //
        // Re-anchoring belongs to a genuine user launch, which is what its
        // rationale actually describes — see MainActivity.onCreate.
        DailyScheduler.ensureScheduled(this, reanchor = false)
        applicationScope.launch {
            DailyScheduler.ensureNotificationScheduled(this@NamesApp, reanchor = false)
        }
    }
}
