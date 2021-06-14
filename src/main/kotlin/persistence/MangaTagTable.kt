package persistence

import org.jetbrains.exposed.sql.Table

object MangaTagTable : Table() {
    val manga = reference("manga", MangaTable)
    val tag = reference("tag", TagTable)
    override val primaryKey = PrimaryKey(manga, tag, name = "PK_MangaTag")
}
