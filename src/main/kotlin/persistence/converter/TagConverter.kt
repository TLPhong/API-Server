package tlp.media.server.komga.persistence.converter

import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.TagEntity
import persistence.TagTable
import tlp.media.server.komga.model.Tag


fun Tag.toTagEntity(): TagEntity = transaction {
    val tag = this@toTagEntity
    TagEntity.find(tag.group, tag.name)
        ?: TagEntity.new {
            group = tag.group
            name = tag.name
        }
}

fun Iterable<Tag>.toTagEntities(): List<TagEntity> = transaction {
    val tags = this@toTagEntities
    val notFoundTags = tags.filter { tag ->
        TagEntity.find(tag.group, tag.name) == null
    }
    val insertedIds = TagTable.batchInsert(notFoundTags) { tag ->
        this[TagTable.group] = tag.group
        this[TagTable.name] = tag.name
    }.map {
        it[TagTable.id].value
    }
    TagEntity.forIds(insertedIds).toList()
}

fun TagEntity.toTag(): Tag = Tag(this.group, this.name)

fun Iterable<TagEntity>.toTags(): List<Tag> = this.map { it.toTag() }
