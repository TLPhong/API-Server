package tlp.media.server.komga.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import tlp.media.server.komga.model.Tag

object TagTable : IntIdTable("tags") {
    val name: Column<String> = varchar("name", length = 255)
    val group: Column<String?> = varchar("group", length = 255).nullable()
}

class TagEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TagEntity>(TagTable) {
        fun fromTag(tag: Tag) = TagEntity.new {
            group = tag.group
            name = tag.name
        }
    }

    var group by TagTable.group
    var name by TagTable.name
}
