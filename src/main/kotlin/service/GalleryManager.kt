package tlp.media.server.komga.service

import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.MangaEntity
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.parser.MangaFolderParser
import tlp.media.server.komga.persistence.converter.toMangaFolder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence


/***
 * Service that handle parsing/recurring parsing
 * TODO: Rename to resolving
 * ====
 * When app init:
 *      call initialize (sync)
 *          -> update mangaFolder list (sync)
 *          sync to db (mangaFolder list) (async)
 * Every 5 mins after first: (async)
 *      -> update mangaFolder list (sync)
 *      -> sync to db (sync)
 */
class GalleryManager private constructor(galleryFolderPath: Path) {
    companion object {
        private val logger = KotlinLogging.logger(GalleryManager::class.java.simpleName)

        private var galleryFolderPath: Path = Paths.get(Constant.galleryPath)
        val instance: GalleryManager = GalleryManager(galleryFolderPath)
    }

    fun getMangaFolders(): List<MangaFolder> = mangaFolderList

    private var mangaFolderList: List<MangaFolder> = emptyList()

    public fun initialize() {
        val parserList = loadFromDisk().toList()
        val loadedFromDb = loadFromDb()
        val syncTypeMap = compareForSyncType(parserList, loadedFromDb)
        val mangaFolderList = loadMangaFolders(parserList, loadedFromDb, syncTypeMap)
        persistManga(mangaFolderList, syncTypeMap)
    }

    private fun loadFromDisk(): Sequence<MangaFolderParser> = Files
        .walk(galleryFolderPath, 1, null)
        .asSequence()
        .filterNotNull()
        .mapNotNull { MangaFolderParser(it) }

    private fun loadFromDb(): List<MangaFolder> = transaction {
        MangaEntity.all().map { it.toMangaFolder() }
    }

    private enum class SyncType {
        CREATED, // found from disk not in db
        DELETED, //
        UNCHANGED // Nothing changed
    }

    private fun compareForSyncType(
        physicalManga: Iterable<MangaFolderParser>,
        persistedMangas:Iterable<MangaFolder>,
    ):Map<String, SyncType> {
        TODO()
    }

    private fun loadMangaFolders(
        parserList: List<MangaFolderParser>,
        loadedFromDb: List<MangaFolder>,
        syncTypeMap: Map<String, SyncType>
    ): List<MangaFolder> {
        TODO()
    }

    private fun persistManga(syncTypeMap: List<MangaFolder>, syncTypeMap1: Map<String, SyncType>) {
        TODO()
    }

    private fun scheduleRefreshing() {
        TODO()
    }
}
