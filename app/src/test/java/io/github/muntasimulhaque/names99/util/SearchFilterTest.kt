package io.github.muntasimulhaque.names99.util

import io.github.muntasimulhaque.names99.data.Name
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFilterTest {

    private val names = listOf(
        Name(1, "الله", "Allah", "The God", "The one truly venerated and worshipped."),
        Name(21, "الْحَكِيم", "Al-Hakeem", "The All-Wise", "The one fully wise in everything."),
        Name(56, "اللَّطِيف", "Al-Lateef", "The Subtle And Kind", "The one who is fully aware of the hidden details."),
        Name(30, "الرَّحْمَٰن", "Ar-Rahmaan", "The Extremely Merciful", "The one possessing tremendous mercy."),
        Name(3, "الْأَعْلَى", "Al-A'laa", "The Most High", "The one who is above everything."),
    )

    @Test
    fun blankQueryReturnsEverything() {
        assertEquals(names, SearchFilter.filter(names, ""))
        assertEquals(names, SearchFilter.filter(names, "   "))
    }

    @Test
    fun matchesTransliterationIgnoringCase() {
        assertEquals(listOf(names[1]), SearchFilter.filter(names, "hakeem"))
        assertEquals(listOf(names[2]), SearchFilter.filter(names, "AL-LAT"))
    }

    @Test
    fun matchesTitleAndMeaning() {
        assertEquals(listOf(names[1]), SearchFilter.filter(names, "all-wise"))
        assertEquals(listOf(names[2]), SearchFilter.filter(names, "hidden details"))
    }

    @Test
    fun matchesArabic() {
        assertEquals(listOf(names[0]), SearchFilter.filter(names, "الله"))
    }

    @Test
    fun matchesExactNumber() {
        assertEquals(listOf(names[1]), SearchFilter.filter(names, "21"))
    }

    @Test
    fun noMatchReturnsEmpty() {
        assertTrue(SearchFilter.filter(names, "zzzz").isEmpty())
    }

    @Test
    fun collapsedVowelsStillMatch() {
        assertEquals(listOf(names[3]), SearchFilter.filter(names, "rahman"))
        assertEquals(listOf(names[3]), SearchFilter.filter(names, "ar rahman"))
    }

    @Test
    fun hyphensSpacesAndApostrophesAreIgnored() {
        assertEquals(listOf(names[2]), SearchFilter.filter(names, "al lateef"))
        // Short keys match broadly while typing; the apostrophized name must be among them.
        assertTrue(names[4] in SearchFilter.filter(names, "alaa"))
    }

    @Test
    fun arabicQueryWithoutMarksFindsVocalizedNames() {
        assertEquals(listOf(names[2]), SearchFilter.filter(names, "اللطيف"))
    }
}
