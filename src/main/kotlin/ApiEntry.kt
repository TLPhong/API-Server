package tlp.media.server.komga

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.launch
import mu.KLogger
import mu.KotlinLogging
import tlp.media.server.komga.model.Page
import tlp.media.server.komga.service.ImageReaderService
import tlp.media.server.komga.service.MangaFolderService
import tlp.media.server.komga.service.UsageLoggingService
import java.net.URLConnection
import java.nio.file.Path
import kotlin.system.measureTimeMillis

val PipelineContext<Unit, ApplicationCall>.logger: KLogger
    get() = KotlinLogging.logger("Route ${this.call.request.uri}")

fun Application.apiModule() {
    val mangaFolderService = MangaFolderService.instance
    val imageReaderService = ImageReaderService.instance
    val usageLoggerService = UsageLoggingService.instance
    val jsonMapper = jacksonObjectMapper()

    routing {
        route("api") {
            get("latest") {
                val pageNum = (call.request.queryParameters["page"] ?: "1").toInt()
                val pageSize = (call.request.queryParameters["size"] ?: "20").toInt()
                val pagedMangaFolders = mangaFolderService.getLatestMangas(pageNum = pageNum, pageSize = pageSize)
                val mangasPage = mangaFolderService.convertToMangasPage(pagedMangaFolders)
                call.respondText(contentType = Json) {
                    logger.info { "List latest for ${pagedMangaFolders.items.size} mangas" }
                    @Suppress("BlockingMethodInNonBlockingContext")
                    jsonMapper.writeValueAsString(mangasPage)
                }
                launch { usageLoggerService.listingManga(pagedMangaFolders.items) }
            }

            get("popular") {
                val pageNum = (call.request.queryParameters["page"] ?: "1").toInt()
                val pageSize = (call.request.queryParameters["size"] ?: "20").toInt()
                val pagedMangaFolders = mangaFolderService.getRandomMangaList(pageNum = pageNum, pageSize = pageSize)
                val mangasPage = mangaFolderService.convertToMangasPage(pagedMangaFolders)
                call.respondText(contentType = Json) {
                    logger.info { "List popular for ${pagedMangaFolders.items.size} mangas" }
                    @Suppress("BlockingMethodInNonBlockingContext")
                    jsonMapper.writeValueAsString(mangasPage)
                }
                launch { usageLoggerService.listingManga(pagedMangaFolders.items) }
            }

            get("search") {
                val query = (call.request.queryParameters["query"] ?: "").toString()
                val pageNum = (call.request.queryParameters["page"] ?: "1").toInt()
                val pageSize = (call.request.queryParameters["size"] ?: "20").toInt()
                val pagedMangaFolders =
                    mangaFolderService.queryMangaFolders(query, pageSize = pageSize, pageNum = pageNum)
                val mangasPage = mangaFolderService.convertToMangasPage(pagedMangaFolders)
                call.respondText(contentType = Json) {
                    logger.info { "Query [$query] result, serve ${pagedMangaFolders.items.size} mangas" }
                    @Suppress("BlockingMethodInNonBlockingContext")
                    jsonMapper.writeValueAsString(mangasPage)
                }
                launch { usageLoggerService.listingManga(pagedMangaFolders.items) }
            }
            // MangaDetail
            route("manga/{id}") {
                val mangaIdKey = AttributeKey<String>("mangaId")
                get {
                    val mangaId = call.attributes[mangaIdKey]
                    val manga = mangaFolderService.getManga(mangaId)
                    call.respondText(contentType = Json) {
                        logger.info { "Serve ${manga.manga.title}" }
                        @Suppress("BlockingMethodInNonBlockingContext")
                        jsonMapper.writeValueAsString(manga)
                    }
                }

                get("pages") {
                    val id = call.attributes[mangaIdKey]
                    val pages = mangaFolderService.getPages(id)
                    call.respondText(contentType = Json) {
                        logger.info { "Listing ${pages.size} pages" }
                        @Suppress("BlockingMethodInNonBlockingContext")
                        jsonMapper.writeValueAsString(
                            pages.map { it.second }
                        )
                    }
                    launch {
                        val mangaTile = mangaFolderService.getTitle(id)
                        usageLoggerService.listingPage(pages, mangaTile)
                    }
                }

                route("{image}") {
                    //Pre-processing
                    val imagePathKey = AttributeKey<Path>("imagePath")
                    intercept(ApplicationCallPipeline.Features) {
                        val mangaId = call.attributes[mangaIdKey]
                        val imageFileName = call.parameters["image"] ?: ""

                        val file = mangaFolderService.getImage(mangaId, imageFileName)
                        if (file != null) {
                            call.attributes.put(imagePathKey, file.toPath())
                        } else {
                            logger.warn { "Image file [$mangaId/$imageFileName] not found" }
                            call.respond(HttpStatusCode.NotFound)
                            finish()
                        }

                        launch {
                            val mangaTitle: String = mangaFolderService.getTitle(mangaId)
                            val mangaPage: Pair<Path, Page>? = mangaFolderService.getPage(mangaId, imageFileName)
                            if (mangaPage != null) {
                                usageLoggerService.servingPage(
                                    page = mangaPage.second,
                                    path = mangaPage.first,
                                    mangaName = mangaTitle
                                )
                            } else {
                                logger.warn { "Image file [$mangaId/$imageFileName] not found" }
                            }
                        }
                    }
                    //Serve image
                    get {
                        val path = call.attributes.get(key = imagePathKey)
                        val time = measureTimeMillis {
                            val contentType = URLConnection.guessContentTypeFromName(path.fileName.toString())
                            val image = imageReaderService.loadImage(path, resized = false)
                            call.respondBytes(ContentType.parse(contentType)) { image }
                        }
                        logger.info { "Serve ${path.fileName} in [${time}millis]" }
                    }
                    //Serve thumbnail
                    get("thumbnail") {
                        val path = call.attributes.get(key = imagePathKey)
                        val time = measureTimeMillis {
                            val contentType = URLConnection.guessContentTypeFromName(path.fileName.toString())
                            val image = imageReaderService.loadImage(path, resized = true)
                            call.respondBytes(ContentType.parse(contentType)) { image }
                        }
                        logger.info { "Serve ${path.fileName} compressed in [${time}millis] " }
                    }
                }

                /***
                 * Validate manga ID beforehand
                 */
                intercept(ApplicationCallPipeline.Features) {
                    val mangaId = this.call.parameters["id"]
                    if (mangaId == null) {
                        logger.warn { "Manga ID is missing" }
                        call.respond(HttpStatusCode.BadRequest, "Missing manga ID")
                        finish()
                    } else {
                        if (!mangaFolderService.containsKey(mangaId)) {
                            logger.warn { "ID [$mangaId] not found" }
                            call.respond(HttpStatusCode.NotFound)
                            finish()
                        }
                        call.attributes.put(mangaIdKey, mangaId)
                    }
                }
            }
        }
        route("areYouAlive"){
            get {
                call.respondText(ContentType.Text.Plain){ "yea" }
            }
        }
    }
}
