package io.github.muntasimulhaque.names99.util

import io.github.muntasimulhaque.names99.data.Name

object SearchFilter {

    /** Matches transliteration, title, meaning (case-insensitive), Arabic, or the exact number. */
    fun filter(names: List<Name>, query: String): List<Name> {
        val q = query.trim()
        if (q.isEmpty()) return names
        val number = q.toIntOrNull()
        return names.filter {
            it.transliteration.contains(q, ignoreCase = true) ||
                it.title.contains(q, ignoreCase = true) ||
                it.meaning.contains(q, ignoreCase = true) ||
                it.arabic.contains(q) ||
                it.number == number
        }
    }
}
