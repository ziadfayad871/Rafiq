package com.rafiq.launcher.data.adhkar

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class AdhkarItem(
    val id: String,
    val title: String?,
    val text: String,
    val count: Int,
    val countText: String,
    val source: String,
    val benefit: String
)

data class AdhkarCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val items: List<AdhkarItem>
)

data class AdhkarData(
    val source: String,
    val categories: List<AdhkarCategory>
)

class AdhkarRepository(private val context: Context) {
    private val mutex = Mutex()
    @Volatile
    private var cached: AdhkarData? = null

    suspend fun load(): AdhkarData {
        cached?.let { return it }
        return mutex.withLock {
            cached ?: withContext(Dispatchers.IO) {
                parse(loadAsset()).also { cached = it }
            }
        }
    }

    private fun loadAsset(): String =
        context.assets.open("adhkar.json").bufferedReader(Charsets.UTF_8).use { it.readText() }

    private fun parse(raw: String): AdhkarData {
        val root = JSONObject(raw)
        val categoriesArray = root.getJSONArray("categories")
        val categories = ArrayList<AdhkarCategory>(categoriesArray.length())
        for (i in 0 until categoriesArray.length()) {
            val categoryObject = categoriesArray.getJSONObject(i)
            val itemsArray = categoryObject.getJSONArray("items")
            val items = ArrayList<AdhkarItem>(itemsArray.length())
            for (j in 0 until itemsArray.length()) {
                val item = itemsArray.getJSONObject(j)
                items += AdhkarItem(
                    id = item.getString("id"),
                    title = item.optString("title").takeIf { it.isNotBlank() },
                    text = item.getString("text"),
                    count = item.optInt("count", 1),
                    countText = item.optString("countText", ""),
                    source = item.optString("source", ""),
                    benefit = item.optString("benefit", "")
                )
            }
            categories += AdhkarCategory(
                id = categoryObject.getString("id"),
                title = categoryObject.getString("title"),
                subtitle = categoryObject.optString("subtitle", ""),
                icon = categoryObject.optString("icon", "star"),
                items = items
            )
        }
        return AdhkarData(
            source = root.optString("source", ""),
            categories = categories
        )
    }
}
