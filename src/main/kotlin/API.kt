package tlp.media.server.komga

import com.beust.klaxon.Klaxon
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import mu.KotlinLogging


fun Application.apiModule() {
    val klaxon = Klaxon()
    val logger = KotlinLogging.logger("API module")

    val mangaFolderService = MangaFolderService.instance
    routing {
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
                get {
                    val id = call.parameters["id"] ?: ""
                    val manga = mangaFolderService.getManga(id)
                    if (manga != null) {
                        call.respondText(contentType = ContentType.Application.Json) {
                            logger.info { "${call.request.uri} Serve ${manga.manga.title}" }
                            klaxon.toJsonString(manga)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                get("{image}") {
                    val mangaId = call.parameters["id"] ?: ""
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
                    val id = call.parameters["id"] ?: ""
                    val pages = mangaFolderService.getPages(id)
                    if(pages != null){
                        call.respondText(contentType = ContentType.Application.Json) {
                            logger.info { "${call.request.uri} listing ${pages.size} pages" }
                            klaxon.toJsonString(pages)
                        }
                    }else{
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }


    }
}
