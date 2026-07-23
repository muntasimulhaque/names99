package io.github.muntasimulhaque.names99.util

import java.util.TimeZone

/** Deterministic daily-name rotation: local epoch day % 99, shared by banner, widget, notification. */
object DailyName {

    const val COUNT = 99
    private const val DAY_MILLIS = 86_400_000L

    /** Returns the name number (1..99) for the local calendar day containing [nowMillis]. */
    fun numberFor(nowMillis: Long, timeZone: TimeZone = TimeZone.getDefault()): Int {
        val localDays = (nowMillis + timeZone.getOffset(nowMillis)) / DAY_MILLIS
        return (localDays % COUNT).toInt() + 1
    }
}
