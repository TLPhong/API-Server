package persistence

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
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
    companion object : EntityClass<String, MangaEntity>(MangaTable)

    var title by MangaTable.title
    var uploadTime by MangaTable.uploadTime
    var downloaded by MangaTable.downloaded
    var uploadBy by MangaTable.uploadBy
    var description by MangaTable.description
    val images by ImageEntity referrersOn ImageTable.manga
    var tags by TagEntity via MangaTagTable
}


