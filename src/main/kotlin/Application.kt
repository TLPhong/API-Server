package tlp.media.server.komga

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.MangaTable
import persistence.DatabaseConfig
import persistence.ImageTable
import persistence.MangaTagTable
import persistence.TagTable

private fun configure() {
    DatabaseConfig.initialize()
    transaction { SchemaUtils.create(MangaTable, ImageTable, TagTable, MangaTagTable) }
}

fun main() {
    configure()
}
