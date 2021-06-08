package tlp.media.server.komga.database

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object MangaFolderTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("id", length = 50).entityId()
}

class MangaFolderEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, MangaFolderEntity>(MangaFolderTable)
}


