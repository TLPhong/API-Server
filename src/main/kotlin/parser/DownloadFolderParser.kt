package tlp.media.server.komga.parser

import io.ktor.util.*
import mu.KotlinLogging
import tlp.media.server.komga.model.MangaFolder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class DownloadFolderParser(val rootFolder: Path) {

    private val logger = KotlinLogging.logger("Folder parser")

    init{
        if (!Files.isDirectory(rootFolder)) {
            error("Path is not a folder: $rootFolder" )
        }

    }

    fun parse(): List<MangaFolder> {
        logger.info { "Start indexing $rootFolder" }
        val mangaFolderList = Files.list(rootFolder)
            .asSequence()
            .onEach {
                logger.info { "Process ${it.fileName}" }
            }
            .mapNotNull {
                try {
                    logger.info { "Validate folder ${it.fileName}" }
                    FolderParser(it)
                } catch (exception: Exception) {
                    logger.error(exception)
                    null
                }
            }
            .mapNotNull {
                try {
                    logger.info { "Parsing ${it.rootPath.fileName}" }
                    it.parse()
                } catch (exception: Exception) {
                    logger.error(exception)
                    null
                }
            }
            .onEach { logger.info{"Complete parse ${it.meta.title}"} }
            .toList()
        logger.info { "Finished parse ${mangaFolderList.size} folder" }
        return mangaFolderList
    }


}
