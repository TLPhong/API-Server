package tlp.media.server.komga.parser

import io.ktor.util.*
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import mu.KotlinLogging
import tlp.media.server.komga.model.MangaFolder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence
import kotlin.streams.toList

class DownloadFolderParser(val rootFolder: Path) {

    private val logger = KotlinLogging.logger("Folder parser")

    init {
        if (!Files.isDirectory(rootFolder)) {
            error("Path is not a folder: $rootFolder")
        }

    }

    fun parse(useProgressBar: Boolean = true, showLogLines: Boolean = false): List<MangaFolder> {
        val mangasFolderList = Files.list(rootFolder).toList()
        val progressBar = ProgressBarBuilder()
            .setTaskName("Parse")
            .setInitialMax(mangasFolderList.size.toLong())
            .setStyle(ProgressBarStyle.ASCII)
            .build()
        progressBar.extraMessage = "Reading..."
        //------------------------------------------
        if (showLogLines) logger.info { "Start indexing $rootFolder" }
        val mangaFolderList = mangasFolderList
            .onEach {
                if (showLogLines) logger.info { "Process ${it.fileName}" }
            }
            .mapNotNull {
                try {
                    if (showLogLines) logger.info { "Validate folder ${it.fileName}" }
                    FolderParser(it)
                } catch (exception: Exception) {
                    logger.error(exception)
                    null
                }
            }
            .mapNotNull {
                try {
                    if (showLogLines) logger.info { "Parsing ${it.rootPath.fileName}" }
                    it.parse()
                } catch (exception: Exception) {
                    logger.error(exception)
                    null
                }
            }
            .onEach {
                if (showLogLines) logger.info { "Complete parse ${it.title}" }
                if (useProgressBar) progressBar.step()
            }
        if (showLogLines) logger.info { "Finished parse ${mangaFolderList.size} folder" }
        if (useProgressBar) progressBar.extraMessage = "Finished"
        progressBar.close()
        return mangaFolderList
    }


}
