package tlp.media.server.komga.service

import logging.UsageLogFacade
import mu.KotlinLogging
import tlp.media.server.komga.logging.UsageLogFacadeImpl
import tlp.media.server.komga.logging.toItem
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.Page
import java.nio.file.Path

class UsageLoggingService {
    private val logger = KotlinLogging.logger(this::class.java.simpleName)


    companion object {
        val instance = UsageLoggingService()
    }

    private val loggingFacade: UsageLogFacade;
    init {
        loggingFacade = UsageLogFacadeImpl()
    }

    public fun servingPage(page: Page, path: Path, mangaName: String){
        logger.info { "Serving item $mangaName" }
        val item = (path to page).toItem(mangaName)
        loggingFacade.itemBeServing(item)
    }

}
