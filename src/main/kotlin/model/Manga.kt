package tlp.media.server.komga.model

import eu.kanade.tachiyomi.source.model.SManga
import kotlinx.serialization.Serializable
import tlp.media.server.komga.constant.ApiPaths

@Serializable
data class Manga (
    override var artist: String?,
    override var description: String?,
    override var thumbnail_url: String?,
    override var title: String,
    override var url: String
) : SManga {
    companion object {
        fun fromMangaFolder(mangaFolder: MangaFolder): Manga {
            val artist = mangaFolder.meta.getArtist()
            val description = mangaFolder.meta.description
            val thumbnailUrl = "${mangaFolder.thumbnail.second.imageUrl}/thumbnail"
            val title = mangaFolder.title
            val url = ApiPaths.mangaDetail(mangaFolder.id)
            val manga =  Manga(
                artist,
                description,
                thumbnailUrl,
                title,
                url
            )
            val tagsString = mangaFolder.meta.getTagString()
            manga.genre = tagsString

            return manga
        }
    }

    override var author: String? = null
    override var genre: String? = null
    override var status: Int = SManga.COMPLETED
    override var initialized: Boolean = true
}
