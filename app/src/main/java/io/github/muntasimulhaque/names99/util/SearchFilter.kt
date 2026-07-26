package io.github.muntasimulhaque.names99.util

import io.github.muntasimulhaque.names99.data.Name

object SearchFilter {

    private val LATIN_NOISE = Regex("[^a-z0-9]")
    private val ARABIC_MARKS = Regex("[\\u064B-\\u065F\\u0670\\u0653]")

    /**
     * Forgiving Latin key: lowercase, punctuation/spaces dropped, and runs of
     * a repeated letter collapsed — so "rahman", "ar rahman", and "a'laa"
     * find "Ar-Rahmaan" and "Al-A'laa".
     */
    private fun latinKey(s: String): String {
        val stripped = LATIN_NOISE.replace(s.lowercase(), "")
        val sb = StringBuilder(stripped.length)
        for (c in stripped) {
            if (sb.isEmpty() || sb.last() != c) sb.append(c)
        }
        return sb.toString()
    }

    /** Arabic without harakat, so a bare query still finds the vocalized names. */
    private fun arabicKey(s: String): String = ARABIC_MARKS.replace(s, "")

    /** Matches transliteration, title, meaning (case-insensitive), Arabic, or the exact number. */
    fun filter(names: List<Name>, query: String): List<Name> {
        val q = query.trim()
        if (q.isEmpty()) return names
        val number = q.toIntOrNull()
        val lq = latinKey(q)
        val aq = arabicKey(q)
        return names.filter {
            it.transliteration.contains(q, ignoreCase = true) ||
                it.title.contains(q, ignoreCase = true) ||
                it.meaning.contains(q, ignoreCase = true) ||
                (lq.isNotEmpty() && lq in latinKey(it.transliteration)) ||
                it.arabic.contains(q) ||
                (aq.isNotEmpty() && aq in arabicKey(it.arabic)) ||
                it.number == number
        }
    }
}
