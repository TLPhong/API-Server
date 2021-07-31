package tlp.media.server.komga.service

import mu.KotlinLogging
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.MangaEntity
import persistence.MangaTable
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.exception.ParserException
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.parser.MangaFolderParser
import tlp.media.server.komga.persistence.converter.toMangaEntity
import tlp.media.server.komga.persistence.converter.toMangaFolder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.concurrent.thread
import kotlin.streams.asSequence
import kotlin.concurrent.schedule;

/***
 * Service that handle parsing/recurring parsing
 * ====
 * When app init:
 *      call initialize (sync)
 *          -> update mangaFolder list (sync)
 *          sync to db (mangaFolder list) (async)
 * Every 5 minutes after first: (async)
 *      -> update mangaFolder list (sync)
 *      -> sync to db (sync)
 */
class GalleryManager private constructor() {
    companion object {
        private var privateInstance: GalleryManager? = null
        val instance: GalleryManager
            get() {
                if (privateInstance == null) {
                    privateInstance = GalleryManager()
                }
                return privateInstance!!
            }
    }

    private val logger = KotlinLogging.logger(GalleryManager::class.java.simpleName)
    private var galleryFolderPath: Path = Paths.get(Constant.galleryPath)

    fun getMangaFolders(): Map<String, MangaFolder> = mangaFolderList

    private var mangaFolderList: Map<String, MangaFolder> = mapOf()

    fun initialize(waitDbSync: Boolean = false) {
        scheduleGarbageCollector()
        val persistMangaThread = refreshMangas {
            scheduleRefreshing()
        }
        if (waitDbSync) {
            persistMangaThread.join()
        }
    }

    private fun refreshMangas(callback: () -> Unit): Thread {
        logger.trace { "Refreshing manga" }
        logger.trace { "Loading from disk" }
        val parserList = loadFromDisk().toList()
        logger.trace { "Loading from database" }
        val loadedFromDb: List<String> = loadIdListFromDb()
        logger.trace { "Syncing" }
        val syncTypeMap = compareForSyncType(parserList, loadedFromDb)
        mangaFolderList = loadMangaFolders(parserList, syncTypeMap)

        return thread(name = "persist db", priority = 1, isDaemon = true) {
            persistManga(mangaFolderList, syncTypeMap)
            callback()
            logger.trace { "Syncing finished" }
        }
    }

    private fun loadFromDisk(): Sequence<MangaFolderParser> = Files
        .list(galleryFolderPath)
        .asSequence()
        .filterNotNull()
        .map { MangaFolderParser(it) }

    private fun loadIdListFromDb(): List<String> = transaction {
        MangaTable.slice(MangaTable.id).selectAll().map { it[MangaTable.id].value }
    }

    private enum class SyncType {
        CREATED, // found from disk not in db
        DELETED, // not found on disk but found in db
        UNCHANGED // Nothing changed
    }

    private fun compareForSyncType(
        physicalManga: Iterable<MangaFolderParser>,
        persistedIdList: Iterable<String>,
    ): Map<String, SyncType> {
        val currentIdList = physicalManga.map { it.getId() }

        val createdList = currentIdList.subtract(persistedIdList).associateWith { SyncType.CREATED }
        val deletedList = persistedIdList.subtract(currentIdList).associateWith { SyncType.DELETED }
        val unchangedList = currentIdList.intersect(persistedIdList).associateWith { SyncType.UNCHANGED }

        val syncMap: HashMap<String, SyncType> = HashMap()
        syncMap.putAll(createdList + deletedList + unchangedList)
        return syncMap
    }

    private fun loadMangaFolders(
        onDiskMangas: List<MangaFolderParser>,
        syncTypeMap: Map<String, SyncType>
    ): Map<String, MangaFolder> {
        val resultMangaFolders: HashMap<String, MangaFolder> = HashMap()

        val onDiskIdList = onDiskMangas.associateBy { it.getId() }

        for ((id, syncType) in syncTypeMap) {
            when (syncType) {
                SyncType.CREATED -> {
                    try {
                        onDiskIdList.getOrElse(id) {
                            throw Exception("failed to map $id")
                        }.parse()
                    } catch (exception: ParserException) {
                        logger.warn { "Parsing error $id: $exception " }
                        null
                    }
                }
                SyncType.UNCHANGED -> transaction {
                    MangaEntity[id].toMangaFolder()
                }
                SyncType.DELETED -> null
            }?.also { mangaFolder ->
                resultMangaFolders[mangaFolder.id] = mangaFolder
            }
        }
        return resultMangaFolders
    }

    private fun persistManga(mangaFolderList: Map<String, MangaFolder>, syncTypeMap: Map<String, SyncType>) {
        for ((id, mangaFolder) in mangaFolderList) {
            when (syncTypeMap[id]) {
                SyncType.CREATED -> mangaFolder.toMangaEntity()
                SyncType.DELETED -> mangaFolder.toMangaEntity().delete()
                SyncType.UNCHANGED -> {
                    // skip
                }
            }
        }
    }

    private fun scheduleRefreshing() {
        val period = TimeUnit.MINUTES.toMillis(1)

        Timer(true).schedule(period) {
            thread(name = "refreshing manga folders", priority = 1, isDaemon = true) {
                refreshMangas { scheduleRefreshing() }
            }
        }
    }

    /**
     * Move some where else
     */
    private fun scheduleGarbageCollector() {
        val period = TimeUnit.HOURS.toMillis(1)
        Timer(true).schedule(period) { System.gc() }
    }
}
