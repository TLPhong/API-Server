package tlp.media.server.komga.service

import logging.UsageLogFacade
import mu.KotlinLogging
import tlp.media.server.komga.logging.UsageLogFacadeImpl
import tlp.media.server.komga.logging.entity.Action
import tlp.media.server.komga.logging.toItem
import tlp.media.server.komga.logging.toResource
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.Page
import java.nio.file.Path

class UsageLoggingService {
    private val logger = KotlinLogging.logger(this::class.java.simpleName)


    companion object {
        val instance = UsageLoggingService()
    }

    private val loggingFacade: UsageLogFacade
    init {
        loggingFacade = UsageLogFacadeImpl()
    }


    fun removeResource(targetMangaFolder: MangaFolder) {
        val resource = targetMangaFolder.toResource(action = Action.RESOURCE_REMOVE)
        loggingFacade.resourceChanged(resource)
    }

    fun addResource(targetMangaFolder: MangaFolder) {
        val resource = targetMangaFolder.toResource(action = Action.RESOURCE_ADD)
        loggingFacade.resourceChanged(resource)
    }

    fun listingManga(mangas: List<MangaFolder>){
        logger.info { "Listing ${mangas.size} mangas." }
        val resources = mangas.map { it.toResource(action = Action.RESOURCE_LIST) }
        loggingFacade.resourcesBeListing(resources)
    }

    fun listingPage(pages: List<Pair<Path, Page>>, mangaName: String) {
        logger.info { "Listing pages for $mangaName." }
        val items = pages.map { it.toItem(mangaName,action = Action.ITEM_LIST) }
        loggingFacade.itemsBeListing(items)
    }

    fun servingPage(page: Page, path: Path, mangaName: String){
        logger.info { "Serving item $mangaName." }
        val item = (path to page).toItem(mangaName,action = Action.ITEM_SERVE)
        loggingFacade.itemBeServing(item)
    }

}
