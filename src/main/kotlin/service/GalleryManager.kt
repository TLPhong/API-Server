package tlp.media.server.komga.service

import tlp.media.server.komga.model.MangaFolder
import java.nio.file.Path


/***
 *
 * When app init:
 *      call initialize (sync)
 *          -> update mangaFolder list (sync)
 *          sync to db (mangaFolder list) (async)
 * Every 5 mins after first: (async)
 *      -> update mangaFolder list (sync)
 *      -> sync to db (sync)
 */
class GalleryManager private constructor(){
    val mangaFolders: List<MangaFolder>
        get() = _mangaFolders

    private var _mangaFolders: List<MangaFolder> = emptyList()

    fun initialize() {
        TODO()
    }

    private fun loadFromDisk() : List<MangaFolder>{
        TODO()
    }

    private fun loadFromDb() {
        TODO()
    }

    /**
     * sync (mangaFolder list)
     *              -> create: found from disk not in db
     *              -> modified: -> delete old data -> create
     *              -> delete: remove db entry
     */
    private fun syncToDb() {
        TODO()
    }


}
