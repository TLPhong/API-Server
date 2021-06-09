package tlp.media.server.komga.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import tlp.media.server.komga.model.Page
import java.nio.file.Path


object ImageTable : IntIdTable(name = "images", columnName = "id") {
    val pageIndex: Column<Int> = integer("page_index")
    val systemPath: Column<String> = varchar("system_path", length = 255)
    val apiPath: Column<String> = varchar("api_path", length = 255)
    val manga = reference("manga", MangaTable)
}

class ImageEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ImageEntity>(ImageTable) {
        fun newFromPage(page: Page, path: Path) {
            ImageEntity.new {
                pageIndex = page.index
                systemPath = path.toString()
                apiPath = page.imageUrl
            }
        }
    }

    var pageIndex by ImageTable.pageIndex
    var systemPath by ImageTable.systemPath
    var apiPath by ImageTable.apiPath
    val manga by MangaEntity referencedOn ImageTable.manga
}


