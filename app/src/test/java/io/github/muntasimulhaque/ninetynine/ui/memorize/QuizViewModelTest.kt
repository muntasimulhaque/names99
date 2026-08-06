package io.github.muntasimulhaque.ninetynine.ui.memorize

import io.github.muntasimulhaque.ninetynine.data.Name
import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizViewModelTest {

    private val names = (1..20).map { n ->
        Name(n, "arabic$n", "Name-$n", "Title $n", "Meaning $n")
    }

    private fun vm() = QuizViewModel(SavedStateHandle())

    @Test
    fun ensureQuizBuildsTenQuestions() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        assertEquals(10, vm.questions.size)
        assertEquals(0, vm.index)
        assertEquals(0, vm.score)
        assertFalse(vm.finished)
    }

    @Test
    fun ensureQuizNoOpsWhenAlreadyBuilt() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        val first = vm.questions
        vm.ensureQuiz(names, learned = setOf(1))
        assertEquals(first, vm.questions)
    }

    @Test
    fun emptyNamesGiveNoQuiz() {
        val vm = vm()
        vm.ensureQuiz(emptyList(), learned = emptySet())
        assertTrue(vm.questions.isEmpty())
    }

    @Test
    fun selectCorrectAnswerIncrementsScore() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        val correct = vm.questions[0].answerIndex
        assertTrue(vm.select(correct))
        assertEquals(1, vm.score)
    }

    @Test
    fun selectWrongAnswerDoesNotIncrementScore() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        val wrong = (vm.questions[0].answerIndex + 1) % 4
        assertFalse(vm.select(wrong))
        assertEquals(0, vm.score)
    }

    @Test
    fun selectRecordsMissedNames() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        val q = vm.questions[0]
        val wrong = (q.answerIndex + 1) % 4
        vm.select(wrong)
        assertEquals(listOf(q.number), vm.missed)
    }

    @Test
    fun doubleSelectIsIgnored() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        val correct = vm.questions[0].answerIndex
        vm.select(correct)
        val secondResult = vm.select((correct + 1) % 4)
        assertFalse(secondResult)
        assertEquals(1, vm.score)
    }

    @Test
    fun nextAdvancesAndResetsSelection() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        vm.select(vm.questions[0].answerIndex)
        assertEquals(0, vm.index)
        vm.next()
        assertEquals(1, vm.index)
        assertEquals(-1, vm.selected)
        assertFalse(vm.finished)
    }

    @Test
    fun nextOnLastQuestionSetsFinished() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        repeat(vm.questions.lastIndex) {
            vm.select(vm.questions[vm.index].answerIndex)
            vm.next()
        }
        assertEquals(vm.questions.lastIndex, vm.index)
        assertFalse(vm.finished)
        vm.select(vm.questions[vm.index].answerIndex)
        vm.next()
        assertTrue(vm.finished)
    }

    @Test
    fun perfectRoundHasNoMissed() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        for (i in vm.questions.indices) {
            vm.select(vm.questions[i].answerIndex)
            vm.next()
        }
        assertTrue(vm.finished)
        assertEquals(10, vm.score)
        assertTrue(vm.missed.isEmpty())
    }

    @Test
    fun restartResetsEverything() {
        val vm = vm()
        vm.ensureQuiz(names, learned = emptySet())
        vm.select(vm.questions[0].answerIndex)
        vm.next()
        vm.restart(names, learned = emptySet())
        assertEquals(0, vm.index)
        assertEquals(0, vm.score)
        assertEquals(-1, vm.selected)
        assertFalse(vm.finished)
        assertTrue(vm.missed.isEmpty())
        assertEquals(10, vm.questions.size)
    }
}
