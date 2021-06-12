package tlp.media.server.komga

import persistence.DatabaseConfig
import tlp.media.server.komga.parser.GalleryFolderParser
import java.nio.file.Path

private fun configure() {
    DatabaseConfig.initialize()

}

fun main() {
    configure()
    val rootPath = "D:\\Videos\\Porn\\H_H\\HentaiAtHome_1.6.0\\download"
    val mangaFolders = GalleryFolderParser(Path.of(rootPath)).parse()
}
