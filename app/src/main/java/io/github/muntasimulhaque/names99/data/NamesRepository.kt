package io.github.muntasimulhaque.names99.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

object NamesRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var cache: List<Name>? = null
    private val mutex = Mutex()

    suspend fun load(context: Context): List<Name> {
        cache?.let { return it }
        return mutex.withLock {
            cache ?: withContext(Dispatchers.IO) {
                val text = context.assets.open("names.json").bufferedReader().use { it.readText() }
                json.decodeFromString<List<Name>>(text).sortedBy { it.number }
            }.also { cache = it }
        }
    }

    suspend fun byNumber(context: Context, number: Int): Name? =
        load(context).firstOrNull { it.number == number }
}
