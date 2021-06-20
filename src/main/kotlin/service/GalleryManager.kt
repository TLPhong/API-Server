package tlp.media.server.komga.service

import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.MangaEntity
import sun.tools.jstat.ParserException
import tlp.media.server.komga.constant.Constant
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
class GalleryManager private constructor() {
    companion object {
        private val logger = KotlinLogging.logger(GalleryManager::class.java.simpleName)

        private var galleryFolderPath: Path = Paths.get(Constant.galleryPath)
        val instance: GalleryManager = GalleryManager()
    }

    fun getMangaFolders(): Map<String, MangaFolder> = mangaFolderList

    private var mangaFolderList: Map<String, MangaFolder> = mapOf()

    fun initialize() {
        refreshMangas()
        scheduleRefreshing()
    }

    private fun refreshMangas() {
        val parserList = loadFromDisk().toList()
        val loadedFromDb = loadFromDb()
        val syncTypeMap = compareForSyncType(parserList, loadedFromDb)
        mangaFolderList = loadMangaFolders(parserList, loadedFromDb, syncTypeMap)
        persistManga(mangaFolderList, syncTypeMap)
    }

    private fun loadFromDisk(): Sequence<MangaFolderParser> = Files
        .list(galleryFolderPath)
        .asSequence()
        .filterNotNull()
        .mapNotNull { MangaFolderParser(it) }

    private fun loadFromDb(): List<MangaFolder> = transaction {
        MangaEntity.all().map { it.toMangaFolder() }
    }

    private enum class SyncType {
        CREATED, // found from disk not in db
        DELETED, // not found on disk but found in db
        UNCHANGED // Nothing changed
    }

    private fun compareForSyncType(
        physicalManga: Iterable<MangaFolderParser>,
        persistedMangas: Iterable<MangaFolder>,
    ): Map<String, SyncType> {
        val currentIdList = physicalManga.map { it.getId() }
        val persistedIdList = persistedMangas.map { it.id }

        val createdList = currentIdList.subtract(persistedIdList).associateWith { SyncType.CREATED }
        val deletedList = persistedIdList.subtract(currentIdList).associateWith { SyncType.DELETED }
        val unchangedList = currentIdList.intersect(persistedIdList).associateWith { SyncType.UNCHANGED }

        val syncMap: HashMap<String, SyncType> = HashMap()
        syncMap.putAll(createdList + deletedList + unchangedList)
        return syncMap
    }

    private fun loadMangaFolders(
        physicalManga: List<MangaFolderParser>,
        persistedMangas: List<MangaFolder>,
        syncTypeMap: Map<String, SyncType>
    ): Map<String, MangaFolder> {
        val resultMangaFolders: HashMap<String, MangaFolder> = HashMap()

        val currentIdList = physicalManga.associateBy { it.getId() }
        val persistedIdList = persistedMangas.associateBy { it.id }

        for ((id, syncType) in syncTypeMap) {
            val mangaFolder: MangaFolder? = when (syncType) {
                SyncType.CREATED -> {
                    try {
                        currentIdList.getOrElse(id) {
                            throw Exception("failed to map $id")
                        }.parse()
                    } catch (exception: ParserException) {
                        logger.warn { "Parsing error $id: $exception " }
                        null
                    }
                }

                SyncType.DELETED, SyncType.UNCHANGED -> {
                    persistedIdList.getOrElse(id) { throw Exception("failed to map $id") }
                }
            }
            mangaFolder?.let { resultMangaFolders.put(it.id, it) }
        }
        return resultMangaFolders
    }

    private fun persistManga(mangaFolderList: Map<String, MangaFolder>, syncTypeMap: Map<String, SyncType>) {
        for ((id, mangaFolder) in mangaFolderList) {
            when (syncTypeMap[id]) {
                SyncType.CREATED -> mangaFolder.toMangaEntity()
                SyncType.DELETED -> mangaFolder.toMangaEntity().delete()
                else -> {
                    //Skip
                }
            }
        }
    }

    private fun scheduleRefreshing() {

        val period = TimeUnit.SECONDS.toMillis(90)

        Timer(true).schedule(
            period,
            period
        ) {
            thread(isDaemon = true, name = "refresh manga thread", priority = 1) {
                refreshMangas()
            }
        }
    }
}
