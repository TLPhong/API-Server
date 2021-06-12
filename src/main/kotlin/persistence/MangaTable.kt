package persistence

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.media.server.komga.model.MangaFolder

object MangaTable : IdTable<String>("mangas") {
    override val id: Column<EntityID<String>> = varchar("id", length = 100).entityId()
    override val primaryKey = PrimaryKey(id, name = "PK_Manga")
    val title: Column<String> = varchar("title", length = 255)
    val uploadTime: Column<String> = varchar("upload_time", length = 30)
    val downloaded: Column<String> = varchar("downloaded", length = 30)
    val uploadBy: Column<String> = varchar("upload_by", length = 255)
    val description: Column<String> = text("description", eagerLoading = true)
}

class MangaEntity(id: EntityID<String>) : Entity<String>(id) {



    companion object : EntityClass<String, MangaEntity>(MangaTable) {
        fun fromManga(mangaFolder: MangaFolder) {
            val tagEntities = transaction {
                mangaFolder.meta.tags
                    .map { tag ->
                        return@map TagEntity
                            .find {
                                (TagTable.name eq tag.name) and (TagTable.group eq tag.group)
                            }
                            .limit(1)
                            .firstOrNull() ?: TagEntity.fromTag(tag)
                    }
            }
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
                            .find {
                                (ImageTable.manga eq manga.id) and (ImageTable.pageIndex eq image.second.index)
                            }.limit(1)
                            .firstOrNull() ?: ImageEntity.fromPage(image.second, image.first, manga);
                    }
            }

        }
    }

    var title by MangaTable.title
    var uploadTime by MangaTable.uploadTime
    var downloaded by MangaTable.downloaded
    var uploadBy by MangaTable.uploadBy
    var description by MangaTable.description
    val images by ImageEntity referrersOn ImageTable.manga
    var tags by TagEntity via MangaTagTable
}


