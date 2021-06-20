package tlp.media.server.komga.persistence.converter

import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.ImageEntity
import persistence.MangaEntity
import persistence.TagEntity
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.MangaInfo


fun MangaFolder.toMangaEntity() {
    val mangaFolder = this
    //TODO: Performance improvement point
    val tagEntities = mangaFolder.meta.tags.toTagEntities()
    val manga = transaction {

        MangaEntity.findById(mangaFolder.id) ?: MangaEntity.new(mangaFolder.id) {
            title = mangaFolder.title
            uploadTime = mangaFolder.meta.uploadTime
            downloaded = mangaFolder.meta.downloaded
            uploadBy = mangaFolder.meta.uploadBy
            description = mangaFolder.meta.description
        }.apply {
            tags = SizedCollection(tagEntities)
        }
    }

    transaction {
        mangaFolder.images
            .forEach { image ->
                ImageEntity
                    .find(manga.id.value, image.second.index)
                    ?: image.toImageEntity(manga)
            }
    }
}

fun MangaEntity.toMangaFolder(): MangaFolder {
    val metaInfo = MangaInfo(
        title = this.title,
        uploadTime = this.uploadTime,
        uploadBy = this.uploadBy,
        downloaded = this.downloaded,
        tags = this.tags.toTags(),
        description = this.description
    )

    val images = this.images.toPathPagePairs()

    return MangaFolder(
        id = this.id.value,
        meta = metaInfo,
        images = images
    )
}

