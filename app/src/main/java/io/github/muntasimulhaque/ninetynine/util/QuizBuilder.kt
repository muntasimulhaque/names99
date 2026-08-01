package io.github.muntasimulhaque.ninetynine.util

import io.github.muntasimulhaque.ninetynine.data.Name
import kotlin.random.Random

data class QuizQuestion(
    val number: Int,
    val options: List<String>,
    val answerIndex: Int,
)

object QuizBuilder {

    const val DEFAULT_COUNT = 10

    /** Words that carry no meaning of their own when comparing two titles. */
    private val STOP_WORDS = setOf(
        "the", "and", "or", "of", "to", "in", "on", "for", "with", "from", "by",
        "a", "an", "his", "their", "who", "that", "is", "are", "one", "ones",
    )

    private val WORD_NOISE = Regex("[^a-z ]")

    private fun contentWords(title: String): Set<String> =
        WORD_NOISE.replace(title.lowercase(), " ")
            .split(' ')
            .filter { it.isNotBlank() && it !in STOP_WORDS }
            .toSet()

    /**
     * True when one title says everything the other says and no less — "The
     * Guardian" against "The Ever-Watchful Guardian", or "The Bestower"
     * against "The Bestower of Mercy".
     *
     * Offered as alternatives these are not a test of memory but a trick: both
     * answers are defensible, so the reader is marked wrong for knowing the
     * meaning. Roughly one round in eleven contained a pair like this.
     */
    private fun ambiguousAgainst(answer: Set<String>, other: Set<String>): Boolean =
        other.isNotEmpty() && (answer.containsAll(other) || other.containsAll(answer))

    /** Builds [count] multiple-choice questions: pick the correct title for a name. */
    fun build(all: List<Name>, count: Int = DEFAULT_COUNT, random: Random = Random): List<QuizQuestion> {
        val titles = all.map { it.title }.distinct()
        val words = titles.associateWith(::contentWords)
        return all.shuffled(random).take(count.coerceAtMost(all.size)).map { name ->
            val answerWords = words[name.title] ?: contentWords(name.title)
            val usable = titles.filter {
                it != name.title && !ambiguousAgainst(answerWords, words.getValue(it))
            }
            // If a name is so generic that too few titles are safe, fall back
            // to any other title rather than showing fewer than four options.
            val pool = if (usable.size >= 3) usable else titles.filter { it != name.title }
            val options = (pool.shuffled(random).take(3) + name.title).shuffled(random)
            QuizQuestion(
                number = name.number,
                options = options,
                answerIndex = options.indexOf(name.title),
            )
        }
    }
}
