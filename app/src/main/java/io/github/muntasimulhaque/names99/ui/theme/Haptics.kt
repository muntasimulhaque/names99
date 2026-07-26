package io.github.muntasimulhaque.names99.ui.theme

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Quiet, meaningful haptics. Uses view-level feedback constants so it works
 * from minSdk 24 and automatically respects the system haptics setting.
 */
class Haptics(private val view: View) {

    /** A featherweight tick: page settle, card flip. */
    fun tick() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    /** Positive acknowledgement: marked as learned, correct answer. */
    fun confirm() {
        view.performHapticFeedback(
            if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.CONFIRM
            else HapticFeedbackConstants.CONTEXT_CLICK
        )
    }

    /** Negative acknowledgement: wrong quiz answer. */
    fun reject() {
        view.performHapticFeedback(
            if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.REJECT
            else HapticFeedbackConstants.LONG_PRESS
        )
    }
}

@Composable
fun rememberHaptics(): Haptics {
    val view = LocalView.current
    return remember(view) { Haptics(view) }
}
