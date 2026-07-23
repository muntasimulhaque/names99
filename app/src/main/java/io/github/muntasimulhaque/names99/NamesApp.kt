package io.github.muntasimulhaque.names99

import android.app.Application
import io.github.muntasimulhaque.names99.daily.DailyScheduler
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
        DailyScheduler.ensureScheduled(this)
        // Re-arm the daily notification if it was lost (force-stop, work cancellation).
        applicationScope.launch { DailyScheduler.ensureNotificationScheduled(this@NamesApp) }
    }
}
