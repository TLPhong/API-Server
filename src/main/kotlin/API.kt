package tlp.media.server.komga

import com.beust.klaxon.Klaxon
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import mu.KLogger
import mu.KotlinLogging

open class ApiSubject(
    val logger: KLogger
)

class MangaApiSubject(
    logger: KLogger,
    val mangaFolder: MangaFolderService
) : ApiSubject(logger)

fun Application.apiModule() {
    val klaxon = Klaxon()
    val mangaFolderService = MangaFolderService.instance
    routing {
        var logger = KotlinLogging.logger("API Call")
        intercept(ApplicationCallPipeline.Setup) {
            logger = KotlinLogging.logger("API ${call.request.uri}")
        }
        route("api") {
            get("latest") {
                val pageNum = (call.request.queryParameters["page"] ?: "0").toInt()
                val pageSize = (call.request.queryParameters["size"] ?: "20").toInt()
                val mangas = mangaFolderService.getLatestMangas(pageNum, pageSize)
                call.respondText(contentType = ContentType.Application.Json) {
                    logger.info { "${call.request.uri} List latest for ${mangas.mangas.size} mangas" }
                    klaxon.toJsonString(mangas)
                }
            }


            route("manga/{id}") {
                val mangaIdKey = AttributeKey<String>("mangaId")

                get {
                    val mangaId = call.attributes[mangaIdKey]
                    val manga = mangaFolderService.getManga(mangaId)

                    call.respondText(contentType = ContentType.Application.Json) {
                        logger.info { "${call.request.uri} Serve ${manga.manga.title}" }
                        klaxon.toJsonString(manga)
                    }
                }

                get("{image}") {
                    val mangaId = call.attributes[mangaIdKey]
                    val imageFileName = call.parameters["image"] ?: ""
                    val file = mangaFolderService.getImage(mangaId, imageFileName)
                    if (file != null) {
                        logger.info { "${call.request.uri} served" }
                        call.respondFile(file)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                get("pages") {
                    val id = call.attributes[mangaIdKey]
                    val pages = mangaFolderService.getPages(id)
                    call.respondText(contentType = ContentType.Application.Json) {
                        logger.info { "${call.request.uri} listing ${pages.size} pages" }
                        klaxon.toJsonString(pages)
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
