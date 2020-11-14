package tlp.media.server.komga.service

import tlp.media.server.komga.model.*
import tlp.media.server.komga.parser.DownloadFolderParser
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.random.Random

class MangaFolderService private constructor() {
    companion object {
        val instance = MangaFolderService()
    }

    private val downloadDir = """D:\Videos\Porn\H_H\HentaiAtHome_1.6.0\download"""
    private var mangaFolders: Map<String, MangaFolder> = DownloadFolderParser(Paths.get(downloadDir))
        .parse()
        .map { it.id to it }
        .toMap()
    private val seed by lazy {
        val currTime = Calendar.getInstance().timeInMillis
        currTime / (1000 * 3600)
    }

    fun searchManga(query: String, pageNum: Int, pageSize: Int = 20): MangasPage {
        val chunked = mangaFolders
            .filter { entry -> entry.value.title.contains(query, ignoreCase = true) }
            .map { it.value }
            .chunked(pageSize)

        val mangaList = chunked[pageNum - 1].map {
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


    fun getMangasList(pageNum: Int, pageSize: Int = 20): MangasPage {
        val mangas = mangaFolders
            .map { it.value }

        return getPagedMangasPage(mangas, pageNum, pageSize)
    }

    fun getRandomMangaList(pageNum: Int, pageSize: Int = 20): MangasPage {
        val mangas = mangaFolders
            .map { it.value }
            .shuffled(Random(seed))

        return getPagedMangasPage(mangas, pageNum, pageSize);
    }

    fun getLatestMangas(pageNum: Int, pageSize: Int = 20): MangasPage {
        val mangas = mangaFolders
            .map { it.value }
            .sortedByDescending { it.meta.downloaded }

        return getPagedMangasPage(mangas, pageNum, pageSize)
    }

    private fun getPagedMangasPage(mangas: List<MangaFolder>, pageNum: Int, pageSize: Int): MangasPage {
        val chunked = mangas.chunked(pageSize)
        val chunkIndex = pageNum - 1
        val mangaList = chunked[chunkIndex].map {
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
