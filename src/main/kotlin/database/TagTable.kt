package tlp.media.server.komga.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object TagTable : IntIdTable("tags") {
    val name: Column<String> = varchar("name", length = 255)
    val group: Column<String> = varchar("group", length = 255)
}

class TagEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TagEntity>(ImageTable)

    val group by TagTable.group
    val name by TagTable.name
}
