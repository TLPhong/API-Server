package tlp.media.server.komga.parser

import io.ktor.util.*
import mu.KotlinLogging
import tlp.media.server.komga.exception.ParserException
import tlp.media.server.komga.model.MangaFolder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class GalleryFolderParser(private val rootFolder: Path) {

    private val logger = KotlinLogging.logger("Folder parser")
    private val mangaFolderPaths: List<Path>

    init {
        if (!Files.isDirectory(rootFolder)) {
            error("Path is not a folder: $rootFolder")
        }
        mangaFolderPaths = Files.list(rootFolder).toList().filterNotNull()
    }

    fun parse(showDetailLog: Boolean = false): List<MangaFolder> {
        //------------------------------------------
        logger.info { "Start parsing $rootFolder" }
        val mangaFolderList = mangaFolderPaths
            .onEach {
                if (showDetailLog) logger.info { "Process ${it.fileName}" }
            }
            .mapNotNull {
                try {
                    if (showDetailLog) logger.debug { "Validate folder ${it.fileName}" }
                    val folderParser = MangaFolderParser(it)
                    if (showDetailLog) logger.debug { "Parsing ${it.fileName}" }

                    return@mapNotNull folderParser.parse()
                } catch (parserException: ParserException) {
                    logger.warn("Parsing error: ${parserException.message}")
                } catch (exception: Exception) {
                    logger.error(exception)
                }
                return@mapNotNull null
            }
            .onEach {
                if (showDetailLog) logger.info { "Complete parse ${it.title}" }
            }
            .toList()
        logger.info { "Finished parsing ${mangaFolderList.size} folder" }
        return mangaFolderList
    }


}
