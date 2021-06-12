package tlp.media.server.komga

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.*
import tlp.media.server.komga.parser.MangaFolderParser
import java.nio.file.Path

private fun configure() {
    DatabaseConfig.initialize()
    transaction { SchemaUtils.create(MangaTable, ImageTable, TagTable, MangaTagTable) }
}

fun main() {
    configure()
    val rootPath = "D:\\Videos\\Porn\\H_H\\HentaiAtHome_1.6.0\\download\\[KAZAMA DoJo (Mucc)] Isekai no Onnanoko ni Job Change Shite Moraitai I Want This Woman From Anoth... [1926601]"
    val mangaFolder = MangaFolderParser(Path.of(rootPath)).parse()
    MangaEntity.fromManga(mangaFolder)
}
