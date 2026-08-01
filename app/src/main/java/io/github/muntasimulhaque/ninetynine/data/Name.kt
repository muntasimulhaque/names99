package io.github.muntasimulhaque.ninetynine.data

import kotlinx.serialization.Serializable

@Serializable
data class Name(
    val number: Int,
    val arabic: String,
    val transliteration: String,
    val title: String,
    val meaning: String,
    val note: String? = null
)
