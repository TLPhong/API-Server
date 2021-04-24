package tlp.media.server.komga.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaWithChapter(val manga: Manga, val chapter: Chapter)
