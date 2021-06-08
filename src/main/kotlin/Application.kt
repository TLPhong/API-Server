package tlp.media.server.komga

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.database.MangaFolderTable
import database.DatabaseConfig

private fun configure() {
    DatabaseConfig.initialize()
    transaction { SchemaUtils.create(MangaFolderTable) }
}

fun main() {
    configure()
    embeddedServer(
        Netty,
        port = Constant.port,
        watchPaths = listOf("""Media/KomgaAPI"""),
        module = Application::apiModule
    ).start(wait = true)
}
