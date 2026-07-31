package io.github.muntasimulhaque.names99.util

import io.github.muntasimulhaque.names99.data.Name
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class QuizBuilderTest {

    private fun fakeNames(): List<Name> = (1..99).map {
        Name(
            number = it,
            arabic = "Arabic$it",
            transliteration = "Translit$it",
            title = "Title$it",
            meaning = "Meaning$it",
        )
    }

    @Test
    fun buildsTenQuestionsByDefault() {
        assertEquals(10, QuizBuilder.build(fakeNames()).size)
    }

    @Test
    fun everyQuestionHasFourUniqueOptionsAndAValidAnswerIndex() {
        QuizBuilder.build(fakeNames(), random = Random(42)).forEach { question ->
            assertEquals(4, question.options.size)
            assertEquals(4, question.options.distinct().size)
            assertTrue(question.answerIndex in 0..3)
        }
    }

    @Test
    fun theOptionAtAnswerIndexIsTheNamesTitle() {
        val names = fakeNames()
        QuizBuilder.build(names, random = Random(7)).forEach { question ->
            val correct = names.first { it.number == question.number }.title
            assertEquals(correct, question.options[question.answerIndex])
        }
    }

    @Test
    fun distractorsNeverEqualTheAnswer() {
        val names = fakeNames()
        QuizBuilder.build(names, random = Random(13)).forEach { question ->
            val answer = question.options[question.answerIndex]
            question.options.forEachIndexed { index, option ->
                if (index != question.answerIndex) assertTrue(option != answer)
            }
        }
    }

    @Test
    fun sameSeedGivesSameQuiz() {
        val first = QuizBuilder.build(fakeNames(), random = Random(1)).map { it.number }
        val second = QuizBuilder.build(fakeNames(), random = Random(1)).map { it.number }
        assertEquals(first, second)
    }

    /**
     * A distractor that says everything the answer says, and no less, is not a
     * harder question — it is an unfair one. "The Guardian" offered against
     * "The Ever-Watchful Guardian" marks the reader wrong for being right.
     */
    @Test
    fun neverOffersATitleThatContainsOrIsContainedByTheAnswer() {
        val names = listOf(
            Name(17, "a", "t17", "The Guardian", "m"),
            Name(33, "b", "t33", "The Ever-Watchful Guardian", "m"),
            Name(77, "c", "t77", "The Guardian Lord", "m"),
            Name(78, "d", "t78", "The Bestower", "m"),
            Name(31, "e", "t31", "The Bestower of Mercy", "m"),
            Name(81, "f", "t81", "The Judge", "m"),
            Name(48, "g", "t48", "The Judge And Opener", "m"),
        ) + (10..60).map { Name(it, "x$it", "tx$it", "Distinct Epithet $it", "m") }

        // Every seed, every question: no option may subsume another.
        repeat(50) { seed ->
            QuizBuilder.build(names, count = 10, random = Random(seed)).forEach { question ->
                val answer = words(question.options[question.answerIndex])
                question.options.forEachIndexed { index, option ->
                    if (index == question.answerIndex) return@forEachIndexed
                    val other = words(option)
                    assertTrue(
                        "seed $seed: \"$option\" is ambiguous against the answer",
                        !answer.containsAll(other) && !other.containsAll(answer),
                    )
                }
            }
        }
    }

    /** The same content-word reduction QuizBuilder uses, restated for the test. */
    private fun words(title: String): Set<String> =
        title.lowercase().split(' ')
            .filter { it.isNotBlank() && it !in setOf("the", "and", "or", "of", "to", "in", "on", "for", "with", "from", "by", "a", "an", "his", "their", "who", "that", "is", "are", "one", "ones") }
            .toSet()
}
