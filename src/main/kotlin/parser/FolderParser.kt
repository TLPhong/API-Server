package tlp.media.server.komga.parser

import io.ktor.util.*
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.Page
import java.nio.file.Files
import java.nio.file.Path

class FolderParser(val rootPath: Path) {

    private lateinit var metaFile: Path
    private lateinit var imageList: List<Path>
    private var id: String
    private val baseUrl = Constant.baseUrl

    init {
        validateIsFolder()
        validateContent()
        id = getId()
    }

    fun parse(): MangaFolder{
        val pages = imageList
            .sorted()
            .mapIndexed { index, path ->
                val fileName = path.fileName
                val url = "$baseUrl/manga/$id/$fileName"
                path to Page(index,imageUrl = url)
            }
        val galleryInfo = GalleryInfoParser(metaFile).parse()
        return MangaFolder(id, galleryInfo, pages)
    }


    private fun getId(): String {
        val fileName = rootPath.fileName!!.toString()
        val startIndex = fileName.lastIndexOf("[")
        val endIndex = fileName.lastIndexOf("]")

        return if (startIndex > 0 && endIndex > 0) {
            fileName.substring(startIndex + 1, endIndex)
        }else{
            error("Dir name missing ID")
        }
    }

    private fun validateIsFolder() {
        if (!Files.exists(rootPath)) {
            error("Path not exist $rootPath")
        }

        if (!Files.isDirectory(rootPath)) {
            error("Path is not a directory $rootPath")
        }
    }

    private fun validateContent() {
        var metaFile: Path? = null
        val imageList: MutableList<Path> = mutableListOf()

        Files.list(rootPath)
            .forEach { path ->
                if (path.fileName.toString() == "galleryinfo.txt") metaFile = path
                val extension = path.fileName.extension
                if (extension == "png" || extension == "jpg") imageList.add(path)
            }
        if (imageList.isEmpty()) error("Missing image file")
        else this.imageList = imageList.toList()

        if (metaFile == null) error("Missing meta file")
        else this.metaFile = metaFile as Path
    }
}
