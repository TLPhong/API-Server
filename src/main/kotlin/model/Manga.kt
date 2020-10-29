package tlp.media.server.komga.model

import eu.kanade.tachiyomi.source.model.SManga
import tlp.media.server.komga.ApiPaths
import tlp.media.server.komga.parser.Constant

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
            val thumbnailUrl = mangaFolder.thumbnail.second.imageUrl
            val title = mangaFolder.title
            val url = ApiPaths.mangaDetail(mangaFolder.id)
            return Manga(
                artist,
                description,
                thumbnailUrl,
                title,
                url
            )
        }
    }

    override var author: String? = null
    override var genre: String? = null
    override var status: Int = SManga.COMPLETED
    override var initialized: Boolean = true
}
