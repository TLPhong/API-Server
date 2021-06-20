package tlp.media.server.komga.service

import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.model.*
import tlp.media.server.komga.parser.GalleryFolderParser
import java.io.File
import java.nio.file.Paths
import kotlin.random.Random
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule


/**
 * Service to interact with mangaFolder list
 */
class MangaFolderService private constructor() {
    companion object {
        val instance = MangaFolderService()

    }

    private val downloadDir = Constant.galleryPath
    private var mangaFolders: Map<String, MangaFolder>
    private var seed = Random.nextLong()

    init {
        GalleryManager.instance.initialize()
        mangaFolders = GalleryManager.instance.getMangaFolders()
//        scheduleRefreshMangaFolder()
        scheduleRefreshRandom()
    }

    fun searchManga(query: String, pageNum: Int, pageSize: Int = 20): MangasPage {
        val chunked = queryMangaFolders(query)
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

    private fun queryMangaFolders(query: String): List<MangaFolder> {
        return mangaFolders
            .map { entry ->
                val mangaFolder = entry.value
                val tags = mangaFolder.meta.tags.map { it.toString() }
                val queries = query.split(" ")
                var totalMatchScore = 0

                if (mangaFolder.title.contains(query, ignoreCase = true)) {
                    totalMatchScore++
                }

                totalMatchScore += tags.sumOf { tag ->
                    var matchScore = 0

                    if (tag.equals(query, ignoreCase = true)) {
                        matchScore++
                    }

                    matchScore += queries.count { query ->
                        tag.contains(query, ignoreCase = true)
                    }

                    matchScore
                }

                Pair(entry, totalMatchScore)
            }
            .filter { (_, matchScore) -> matchScore > 0 }
            .sortedByDescending { (_, matchScore) -> matchScore }
            .map { (mangaFoldersEntry, _) -> mangaFoldersEntry.value }
    }

    private fun parseMangasFolder(): Map<String, MangaFolder> {
        return GalleryFolderParser(Paths.get(downloadDir))
            .parse(showDetailLog = false).associateBy { it.id }
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
            val hasNext = chunked.size > chunkIndex + 1
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

//    private fun scheduleRefreshMangaFolder() {
//        val period = TimeUnit.SECONDS.toMillis(90)
//
//        Timer(true).schedule(
//            period,
//            period
//        ) {
//            mangaFolders = parseMangasFolder()
//        }
//    }

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
