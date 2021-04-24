package tlp.media.server.komga.model

import kotlinx.serialization.Serializable

@Serializable
data class MangasPage(val mangas: List<MangaWithChapter>, val hasNextPage: Boolean)
