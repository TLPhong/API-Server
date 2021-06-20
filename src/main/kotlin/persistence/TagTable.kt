package persistence

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.media.server.komga.model.Tag

object TagTable : IntIdTable("tags") {
    val name: Column<String> = varchar("name", length = 255)
    val group: Column<String?> = varchar("group", length = 255).nullable()

    init {
        uniqueIndex("unique_tag", group, name)
    }
}

class TagEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TagEntity>(TagTable) {
        fun find(group: String?, name: String): TagEntity? {
            return this.find {
                (TagTable.name eq name) and (TagTable.group eq group)
            }.limit(1).firstOrNull()
        }

        fun find(name: String): SizedIterable<TagEntity> {
            return this.find{
                TagTable.name eq name
            }
        }
    }

    var group by TagTable.group
    var name by TagTable.name
}
