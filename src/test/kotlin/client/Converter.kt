package client

import eu.kanade.tachiyomi.source.model.SManga as TachiyomiManga
import eu.kanade.tachiyomi.source.model.MangasPage as TachiyomiMangasPage
import eu.kanade.tachiyomi.source.model.SChapter as TachiyomiChapter
import eu.kanade.tachiyomi.source.model.Page as TachiyomiPage
import tlp.media.server.komga.model.Chapter
import tlp.media.server.komga.model.Page
import tlp.media.server.komga.model.Manga
import tlp.media.server.komga.model.MangaWithChapter
import tlp.media.server.komga.model.MangasPage

fun MangasPage.toTachiyomiModel(): TachiyomiMangasPage {
    val tachiyomiMangaList = this.mangas.map { it.toTachiyomiModel() }.toList()

    return TachiyomiMangasPage(
        tachiyomiMangaList,
        hasNextPage = this.hasNextPage
    )
}

fun Manga.toTachiyomiModel(): TachiyomiManga{
    return TachiyomiManga.create().apply{
        url = this.url
        title = this.title
        artist = this.artist
        author = this.author
        description = this.description
        genre = this.genre
        status = this.status
        thumbnail_url = this.thumbnail_url
        initialized = this.initialized
    }
}

fun MangaWithChapter.toTachiyomiModel(): TachiyomiManga {
    val manga = this.manga
    return TachiyomiManga.create().let {
        it.url = manga.url
        it.title = manga.title
        it.artist = manga.artist
        it.author = manga.author
        it.description = manga.description
        it.genre = manga.genre
        it.status = manga.status
        it.thumbnail_url = manga.thumbnail_url
        it.initialized = manga.initialized
        it
    }
}


fun Chapter.toTachiyomiModel(): TachiyomiChapter {
    return TachiyomiChapter.create().let {
        it.url = this.url
        it.chapter_number = this.chapter_number
        it.date_upload = this.date_upload
        it.name = this.name
        it.scanlator = this.scanlator
        it
    }
}

fun Page.toTachiyomiModel(): TachiyomiPage {
    return TachiyomiPage(
        index = this.index,
        imageUrl = this.imageUrl,
        url = "",
        uri = null
    )
}

