package org.xmsleep.app.novel

data class Chapter(
    val title: String,
    val content: String
)

data class NovelItem(
    val id: String,
    val title: String,
    val author: String,
    val category: NovelCategory,
    val content: String,
    val chapters: List<Chapter> = emptyList()
)

enum class NovelCategory(val displayName: String) {
    ESSAY("散文"),
    CHENGYU("成语故事"),
    FABLE("寓言"),
    STORY("小说")
}

object NovelData {

    fun getNovelsByCategory(category: NovelCategory): List<NovelItem> = emptyList()

    fun getAllNovels(): List<NovelItem> = emptyList()
}
