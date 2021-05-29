package tlp.media.server.komga

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import mu.KLogger
import mu.KotlinLogging
import tlp.media.server.komga.service.ImageReaderService
import tlp.media.server.komga.service.MangaFolderService
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.*
import kotlinx.serialization.json.*

val PipelineContext<Unit, ApplicationCall>.logger: KLogger
    get() = KotlinLogging.logger("Route ${this.call.request.uri}")

fun Application.apiModule() {
    val mangaFolderService = MangaFolderService.instance
    val imageReaderService = ImageReaderService.instance
    routing {

        route("api") {
            get("latest") {
                val pageNum = (call.request.queryParameters["page"] ?: "1").toInt()
                val pageSize = (call.request.queryParameters["size"] ?: "20").toInt()
                val mangas = mangaFolderService.getLatestMangas(pageNum, pageSize)
                call.respondText(contentType = ContentType.Application.Json) {
                    logger.info { "List latest for ${mangas.mangas.size} mangas" }
                    Json.encodeToString(mangas)
                }
            }

            get("popular") {
                val pageNum = (call.request.queryParameters["page"] ?: "1").toInt()
                val pageSize = (call.request.queryParameters["size"] ?: "20").toInt()
                //TODO popular mangas
                val mangas = mangaFolderService.getRandomMangaList(pageNum, pageSize)
                call.respondText(contentType = ContentType.Application.Json) {
                    logger.info { "List popular for ${mangas.mangas.size} mangas" }
                    Json.encodeToString(mangas)
                }
            }

            get("search") {
                val query = (call.request.queryParameters["query"] ?: "").toString()
                val pageNum = (call.request.queryParameters["page"] ?: "1").toInt()
                val pageSize = (call.request.queryParameters["size"] ?: "20").toInt()
                val mangas = mangaFolderService.searchManga(query, pageNum, pageSize)
                call.respondText(contentType = ContentType.Application.Json) {
                    logger.info { "Query [$query] result, serve ${mangas.mangas.size} mangas" }
                    Json.encodeToString(mangas)
                }
            }
            // MangaDetail
            route("manga/{id}") {
                val mangaIdKey = AttributeKey<String>("mangaId")

                get {
                    val mangaId = call.attributes[mangaIdKey]
                    val manga = mangaFolderService.getManga(mangaId)

                    call.respondText(contentType = ContentType.Application.Json) {
                        logger.info { "Serve ${manga.manga.title}" }
                        Json.encodeToString(manga)
                    }
                }

                get("pages") {
                    val id = call.attributes[mangaIdKey]
                    val pages = mangaFolderService.getPages(id)
                    call.respondText(contentType = ContentType.Application.Json) {
                        logger.info { "Listing ${pages.size} pages" }
                        Json.encodeToString(pages)
                    }
                }

                route("{image}") {
                    val imagePathKey = AttributeKey<Path>("imagePath")
                    intercept(ApplicationCallPipeline.Features) {
                        val mangaId = call.attributes[mangaIdKey]
                        val imageFileName = call.parameters["image"] ?: ""
                        val file = mangaFolderService.getImage(mangaId, imageFileName)
                        if (file != null) {
                            call.attributes.put(imagePathKey, file.toPath())
                        } else {
                            logger.warn { "Image file not found [$mangaId/$imageFileName] not found" }
                            call.respond(HttpStatusCode.NotFound)
                            finish()
                        }
                    }
                    get {
                        val path = call.attributes.get(key = imagePathKey)
                        call.respondBytes(ContentType.parse(Files.probeContentType(path))) {
                            imageReaderService.loadImage(path, resized = false)
                        }
                        logger.info { "Serve ${path.fileName}" }
                    }
                    get("thumbnail") {
                        val path = call.attributes.get(key = imagePathKey)
                        call.respondBytes(ContentType.parse(Files.probeContentType(path))) {
                            imageReaderService.loadImage(path, resized = true)
                        }
                        logger.info { "Serve ${path.fileName} compressed" }

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


    }
}
