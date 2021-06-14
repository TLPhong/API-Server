package tlp.media.server.komga.service

import tlp.media.server.komga.model.MangaFolder
import java.nio.file.Path

class GalleryManager(val rootPath: Path) {
    val mangaFolders: List<MangaFolder>
        get() = _mangaFolders

    private var _mangaFolders: List<MangaFolder> = emptyList()

    fun initialize() {
        TODO()
    }

    private fun loadFromDisk() {
        TODO()
    }

    private fun loadFromDb() {
        TODO()
    }

    private fun syncToDb() {
        TODO()
    }


}
