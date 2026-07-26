package io.github.muntasimulhaque.names99.util

import io.github.muntasimulhaque.names99.data.Name
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckBuilderTest {

    private val names = (1..6).map { n ->
        Name(n, "arabic$n", "Name-$n", "Title $n", "Meaning $n")
    }
    private val learned = setOf(2, 5)

    @Test
    fun emptyNamesGiveEmptyDeck() {
        assertTrue(DeckBuilder.build(emptyList(), learned, includeLearned = true).isEmpty())
    }

    @Test
    fun learnedAreExcludedByDefault() {
        val deck = DeckBuilder.build(names, learned, includeLearned = false)
        assertEquals(setOf(1, 3, 4, 6), deck.toSet())
    }

    @Test
    fun includeLearnedAppendsThemAfterTheUnlearned() {
        val deck = DeckBuilder.build(names, learned, includeLearned = true)
        assertEquals(6, deck.size)
        assertEquals(setOf(1, 3, 4, 6), deck.take(4).toSet())
        assertEquals(setOf(2, 5), deck.drop(4).toSet())
    }

    @Test
    fun everyNameAppearsExactlyOnceWhenIncluded() {
        val deck = DeckBuilder.build(names, learned, includeLearned = true)
        assertEquals((1..6).toSet(), deck.toSet())
        assertEquals(deck.size, deck.distinct().size)
    }

    @Test
    fun sameSeedGivesSameDeck() {
        val a = DeckBuilder.build(names, learned, includeLearned = true, random = Random(7))
        val b = DeckBuilder.build(names, learned, includeLearned = true, random = Random(7))
        assertEquals(a, b)
    }
}
