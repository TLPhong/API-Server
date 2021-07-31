package tlp.media.server.komga.service

import com.sun.org.apache.xpath.internal.operations.Bool
import tlp.media.server.komga.exception.MangaNotFoundException
import tlp.media.server.komga.model.*
import java.io.File
import java.nio.file.Path
import kotlin.random.Random
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule


/**
 * Service to interact with mangaFolder list
 */
class MangaFolderService private constructor() {
    companion object {
        private var privateInstance: MangaFolderService? = null
        val instance: MangaFolderService
            get() {
                if (privateInstance == null) {
                    privateInstance = MangaFolderService()
                }
                return privateInstance!!
            }
    }

    // ID to Object
    private val mangaFolders: Map<String, MangaFolder>
        get() = GalleryManager.instance.getMangaFolders()
    private var seed = Random.nextLong()

    init {
        GalleryManager.instance.initialize(waitDbSync = false)
        scheduleRefreshRandomSeed()
    }

    fun queryMangaFolders(query: String, pageNum: Int, pageSize: Int): Paged<MangaFolder> {
        val queriedMangaFolder = mangaFolders
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
        return Paged.fromAll(queriedMangaFolder, pageIndex = pageNum - 1, pageSize = pageSize)
    }

    fun getRandomMangaList(pageNum: Int, pageSize: Int): Paged<MangaFolder> {
        val randomizedList = mangaFolders
            .map { it.value }
            .shuffled(Random(seed))
        return Paged.fromAll(randomizedList, pageIndex = pageNum - 1, pageSize = pageSize)
    }

    fun getLatestMangas(pageNum: Int, pageSize: Int): Paged<MangaFolder> {
        val latestList = mangaFolders
            .map { it.value }
            .sortedByDescending { it.meta.downloaded }
        return Paged.fromAll(latestList, pageIndex = pageNum - 1, pageSize = pageSize)
    }


    fun getManga(mangaId: String): MangaWithChapter {
        val mangaFolder = mangaFolders[mangaId] ?: error("Manga ID $mangaId not found")
        return MangaWithChapter(
            manga = Manga.fromMangaFolder(mangaFolder),
            chapter = mangaFolder.chapter
        )
    }

    fun getPage(mangaId: String, imageFileName: String): Pair<Path, Page>? {
        return mangaFolders[mangaId]
            ?.let { mangaFolder ->
                mangaFolder.images.find { it.first.fileName.toString() == imageFileName }
            }
    }

    fun getImage(mangaId: String, imageFileName: String): File? {
        return this.getPage(mangaId, imageFileName)?.first?.toFile()
    }

    fun getPages(mangaId: String): List<Pair<Path, Page>> {
        val mangaFolder = mangaFolders[mangaId] ?: error("Manga ID $mangaId not found")
        return mangaFolder.images
    }

    fun containsKey(key: String): Boolean {
        return mangaFolders.containsKey(key)
    }

    fun convertToMangasPage(mangaFolderPaged: Paged<MangaFolder>): MangasPage {
        val mangaList = mangaFolderPaged.items.map {
            MangaWithChapter(
                manga = Manga.fromMangaFolder(it),
                chapter = it.chapter
            )
        }
        return MangasPage(
            mangas = mangaList,
            hasNextPage = mangaFolderPaged.hasNext
        )
    }

    fun getTitle(mangaId: String): String {
        val mangaFolder = mangaFolders[mangaId] ?: throw MangaNotFoundException(mangaId)
        return mangaFolder.title
    }

    /**
     * Regenerate random seed every hour
     */
    private fun scheduleRefreshRandomSeed() {
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
