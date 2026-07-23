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
}
