package io.github.muntasimulhaque.names99.util

import io.github.muntasimulhaque.names99.data.Name
import kotlin.random.Random

object DeckBuilder {

    /**
     * Builds a flashcard deck of name numbers: the unlearned names first
     * (shuffled), then — only when requested — the learned ones (shuffled).
     */
    fun build(
        all: List<Name>,
        learned: Set<Int>,
        includeLearned: Boolean,
        random: Random = Random,
    ): List<Int> {
        val (known, unknown) = all.partition { it.number in learned }
        return unknown.shuffled(random).map { it.number } +
            if (includeLearned) known.shuffled(random).map { it.number } else emptyList()
    }
}
