package tlp.media.server.komga

import tlp.media.server.komga.model.*
import tlp.media.server.komga.parser.DownloadFolderParser
import java.io.File
import java.nio.file.Paths

class MangaFolderService private constructor() {
    companion object {
        val instance = MangaFolderService()
    }

    private val downloadDir = """D:\Videos\Porn\H_H\HentaiAtHome_1.6.0\download"""
    private var mangaFolders: List<MangaFolder> = DownloadFolderParser(Paths.get(downloadDir)).parse()

    fun getLatestMangas(pageNum: Int, pageSize: Int = 20): MangasPage {
        val chunked = mangaFolders
            .sortedByDescending { it.meta.downloaded }
            .chunked(pageSize)
        val mangaList = chunked[pageNum].map {
            MangaWithChapter(
                manga = Manga.fromMangaFolder(it),
                chapter = it.chapter
            )
        }
        val hasNext = chunked.size > pageNum + 1
        return MangasPage(
            mangas = mangaList,
            hasNext
        )
    }

    fun getManga(id: String): MangaWithChapter? {
        val mangaFolder = mangaFolders.find { it.id == id }
        return if (mangaFolder != null) {
            return MangaWithChapter(
                manga = Manga.fromMangaFolder(mangaFolder),
                chapter = mangaFolder.chapter
            )
        } else {
            null
        }
    }

    fun getImage(mangaId: String, imageFileName: String): File? {
        return mangaFolders
            .find { it.id == mangaId }
            ?.let { mangaFolder ->
                val pair = mangaFolder.images.find { it.first.fileName.toString() == imageFileName }
                pair?.let { pair.first.toFile() }
            }
    }

    fun getPages(id: String): List<Page>? {
        val mangaFolder = mangaFolders.find { it.id == id }
        return if (mangaFolder != null) {
            return mangaFolder.images.map { it.second }.toList()
        } else {
            null
        }
    }
}
