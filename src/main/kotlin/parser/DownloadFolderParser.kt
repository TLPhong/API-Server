package tlp.media.server.komga.parser

import io.ktor.util.*
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import mu.KotlinLogging
import tlp.media.server.komga.model.MangaFolder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class DownloadFolderParser(val rootFolder: Path) {

    private val logger = KotlinLogging.logger("Folder parser")

    init {
        if (!Files.isDirectory(rootFolder)) {
            error("Path is not a folder: $rootFolder")
        }

    }

    private fun createProgressBar(total: Number): ProgressBar {
        val progressBar = ProgressBarBuilder()
            .setTaskName("Parse")
            .setInitialMax(total.toLong())
            .setStyle(ProgressBarStyle.ASCII)
            .build()
        progressBar.extraMessage = "Reading..."
        return progressBar
    }

    fun parse(useProgressBar: Boolean = true, showDetailLog: Boolean = false): List<MangaFolder> {
        val mangasFolderList = Files.list(rootFolder).toList()
        val progressBar: ProgressBar? = if(useProgressBar){
            createProgressBar(mangasFolderList.size)
        }else{
            null
        }
        //------------------------------------------
       logger.info { "Start parsing $rootFolder" }
        val mangaFolderList = mangasFolderList
            .onEach {
                if (showDetailLog) logger.info { "Process ${it.fileName}" }
            }
            .mapNotNull {
                try {
                    if (showDetailLog) logger.info { "Validate folder ${it.fileName}" }
                    return@mapNotNull FolderParser(it)
                } catch (parserException: ParserException) {
                    logger.warn("Validation error: ${parserException.message}")
                } catch (exception: Exception) {
                    logger.error(exception)
                }
                return@mapNotNull null

            }
            .mapNotNull {
                try {
                    if (showDetailLog) logger.info { "Parsing ${it.rootPath.fileName}" }
                    return@mapNotNull it.parse()
                } catch (parserException: ParserException) {
                    logger.warn ("Parsing error: ${parserException.message}")
                } catch (exception: Exception) {
                    logger.error(exception)
                }
                return@mapNotNull null
            }
            .onEach {
                if (showDetailLog) logger.info { "Complete parse ${it.title}" }
                progressBar?.step()
            }
        logger.info { "Finished parsing ${mangaFolderList.size} folder" }
        progressBar?.extraMessage = "Finished"
        progressBar?.close()
        return mangaFolderList
    }


}
