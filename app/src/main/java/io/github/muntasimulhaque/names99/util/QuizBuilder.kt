package io.github.muntasimulhaque.names99.util

import io.github.muntasimulhaque.names99.data.Name
import kotlin.random.Random

data class QuizQuestion(
    val number: Int,
    val options: List<String>,
    val answerIndex: Int,
)

object QuizBuilder {

    const val DEFAULT_COUNT = 10

    /** Builds [count] multiple-choice questions: pick the correct title for a name. */
    fun build(all: List<Name>, count: Int = DEFAULT_COUNT, random: Random = Random): List<QuizQuestion> {
        val titles = all.map { it.title }.distinct()
        return all.shuffled(random).take(count.coerceAtMost(all.size)).map { name ->
            val distractors = titles.filter { it != name.title }.shuffled(random).take(3)
            val options = (distractors + name.title).shuffled(random)
            QuizQuestion(
                number = name.number,
                options = options,
                answerIndex = options.indexOf(name.title),
            )
        }
    }
}
