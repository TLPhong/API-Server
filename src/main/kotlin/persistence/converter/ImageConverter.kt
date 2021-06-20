package tlp.media.server.komga.persistence.converter

import org.jetbrains.exposed.sql.transactions.transaction
import persistence.ImageEntity
import persistence.MangaEntity
import tlp.media.server.komga.model.Page
import java.nio.file.Path
import java.nio.file.Paths


fun Pair<Path, Page>.toImageEntity(mangaEntity: MangaEntity): ImageEntity = transaction {
    ImageEntity.new {
        pageIndex = second.index
        systemPath = first.toString()
        apiPath = second.imageUrl
        manga = mangaEntity
    }
}

fun Iterable<Pair<Path, Page>>.toImageEntities(mangaEntity: MangaEntity): List<ImageEntity> = transaction {
    this@toImageEntities.map {
        it.toImageEntity(mangaEntity)
    }
}

fun ImageEntity.toPathPagePair(): Pair<Path, Page> {
    return Paths.get(systemPath) to Page(pageIndex, apiPath)
}

fun Iterable<ImageEntity>.toPathPagePairs(): List<Pair<Path, Page>> = map { it.toPathPagePair() }


