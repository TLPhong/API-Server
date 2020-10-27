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
    private var mangaFolders: Map<String, MangaFolder>
    init {
        mangaFolders =  DownloadFolderParser(Paths.get(downloadDir))
            .parse()
            .map { it.id to it }
            .toMap()
    }

    fun getLatestMangas(pageNum: Int, pageSize: Int = 20): MangasPage {
        val chunked = mangaFolders
            .map { it.value }
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

    fun getManga(mangaId: String): MangaWithChapter {
        val mangaFolder = mangaFolders[mangaId] ?: error("Manga ID $mangaId not found")
        return MangaWithChapter(
            manga = Manga.fromMangaFolder(mangaFolder),
            chapter = mangaFolder.chapter
        )
    }

    fun getImage(mangaId: String, imageFileName: String): File? {
        return mangaFolders[mangaId]
            ?.let { mangaFolder ->
                val pair = mangaFolder.images.find { it.first.fileName.toString() == imageFileName }
                pair?.let { pair.first.toFile() }
            }
    }

    fun getPages(mangaId: String): List<Page> {
        val mangaFolder = mangaFolders[mangaId] ?: error("Manga ID $mangaId not found")
        return mangaFolder.images.map { it.second }.toList()
    }

    val containsKey = mangaFolders::containsKey
}
