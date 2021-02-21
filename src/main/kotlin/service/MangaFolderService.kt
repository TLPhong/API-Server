package tlp.media.server.komga.service

import tlp.media.server.komga.model.*
import tlp.media.server.komga.parser.DownloadFolderParser
import java.io.File
import java.nio.file.Paths
import kotlin.random.Random
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MangaFolderService private constructor() {
    companion object {
        val instance = MangaFolderService()
    }

    private val downloadDir = """D:\Videos\Porn\H_H\HentaiAtHome_1.6.0\download"""
    private var mangaFolders: Map<String, MangaFolder> = parseMangasFolder()
    private var seed = Random.nextLong()

    init {
        scheduleRefreshMangaFolder()
        scheduleRefreshRandom()
    }

    fun searchManga(query: String, pageNum: Int, pageSize: Int = 20): MangasPage {
        val queries = query.split(" ")
        val chunked = queryMangaFolders(queries)
            .chunked(pageSize)

        if (chunked.isNullOrEmpty()) {
            return MangasPage(
                mangas = emptyList(),
                hasNextPage = true
            )
        }

        val mangaList = chunked[pageNum - 1].map {
            MangaWithChapter(
                manga = Manga.fromMangaFolder(it),
                chapter = it.chapter
            )
        }
        val hasNext: Boolean = chunked.size > pageNum + 1

        return MangasPage(
            mangas = mangaList,
            hasNext
        )
    }

    private fun queryMangaFolders(queries: List<String>): List<MangaFolder> {
        return mangaFolders
            .filter { entry ->
                val mangaFolder = entry.value
                return@filter queries.any { query ->
                    var matched = mangaFolder.title.contains(query, ignoreCase = true)

                    if (!matched) {
                        matched = mangaFolder.meta.tags.any { tag ->
                            tag.toString().equals(query, ignoreCase = true) ||
                                    tag.name.equals(query, ignoreCase = true)

                        }
                    }
                    return@any matched
                }
            }
            .map { it.value }
    }

    private fun parseMangasFolder(): Map<String, MangaFolder> {
        return DownloadFolderParser(Paths.get(downloadDir))
            .parse(useProgressBar = false, showDetailLog = false)
            .map { it.id to it }
            .toMap()
    }


    fun getRandomMangaList(pageNum: Int, pageSize: Int = 20): MangasPage {
        val mangas = mangaFolders
            .map { it.value }
            .shuffled(Random(seed))

        return getPagedMangasPage(mangas, pageNum, pageSize)
    }

    fun getLatestMangas(pageNum: Int, pageSize: Int = 20): MangasPage {
        val mangas = mangaFolders
            .map { it.value }
            .sortedByDescending { it.meta.downloaded }

        return getPagedMangasPage(mangas, pageNum, pageSize)
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

    fun containsKey(key: String): Boolean {
        return mangaFolders.containsKey(key)
    }

    private fun getPagedMangasPage(mangas: List<MangaFolder>, pageNum: Int, pageSize: Int): MangasPage {
        val chunked = mangas.chunked(pageSize)

        return if (chunked.isNotEmpty()) {
            val chunkIndex = pageNum - 1
            val mangaList = chunked[chunkIndex].map {
                MangaWithChapter(
                    manga = Manga.fromMangaFolder(it),
                    chapter = it.chapter
                )
            }
            val hasNext = chunked.size > pageNum + 1
            MangasPage(
                mangas = mangaList,
                hasNext
            )
        } else {
            MangasPage(
                mangas = emptyList(),
                hasNextPage = false
            )
        }
    }

    private fun scheduleRefreshMangaFolder() {
        val period = TimeUnit.SECONDS.toMillis(90)

        Timer(true).schedule(
            period,
            period
        ) {
            mangaFolders = parseMangasFolder()
        }
    }

    private fun scheduleRefreshRandom() {
        val period = TimeUnit.HOURS.toMillis(1)
        Timer(true)
            .schedule(
                period,
                period
            ) {
                seed = Random.nextLong()
            }
    }
}
