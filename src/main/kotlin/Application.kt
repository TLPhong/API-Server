package tlp.media.server.komga

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.database.MangaTable
import database.DatabaseConfig
import tlp.media.server.komga.database.ImageTable
import tlp.media.server.komga.database.MangaTagTable
import tlp.media.server.komga.database.TagTable

private fun configure() {
    DatabaseConfig.initialize()
    transaction { SchemaUtils.create(MangaTable, ImageTable, TagTable, MangaTagTable) }
}

fun main() {
    configure()
}
