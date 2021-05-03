package tlp.media.server.komga.parser

import io.ktor.util.*
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.Page
import java.nio.file.Files
import java.nio.file.Path

class FolderParser(val rootPath: Path) {

    private lateinit var metaFile: Path
    private lateinit var imageList: MutableList<Path>
    private var id: String
    private val baseUrl = Constant.baseUrl
    private val metaFileName = "galleryinfo.txt"

    init {
        validateIsFolder()
        validateContent()
        id = getId()
    }

    private fun parserError(message: String): Nothing = throw ParserException(message)

    fun parse(): MangaFolder {
        sortImageList()
        val pages = imageList
            .mapIndexed { index, path ->
                val fileName = path.fileName
                val url = "$baseUrl/manga/$id/$fileName"
                path to Page(index, imageUrl = url)
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
        } else {
            parserError("Dir name missing ID")
        }
    }

    private fun validateIsFolder() {
        if (!Files.exists(rootPath)) {
            parserError("Path not exist $rootPath")
        }

        if (!Files.isDirectory(rootPath)) {
            parserError("Path is not a directory $rootPath")
        }
    }

    private fun sortImageList() {
        val isIntegerFileName = this.imageList
            .map { imagePath ->
                val fileName = imagePath.fileName.toString()
                fileName.substring(0, fileName.lastIndexOf("."))
            }
            .all { fileNameNoExt ->
                fileNameNoExt.toIntOrNull() != null
            }

        if (isIntegerFileName) {
            this.imageList
                .sortBy { imagePath ->
                    val fileName = imagePath.fileName.toString()
                    val fileNoExt = fileName.substring(0, fileName.lastIndexOf("."))
                    fileNoExt.toInt()
                }
        }else{
            this.imageList.sort()
        }

    }

    private fun validateContent() {
        var metaFile: Path? = null
        val imageList: MutableList<Path> = mutableListOf()

        Files.list(rootPath)
            .forEach { path ->
                //Handle meta file
                if (path.fileName.toString() == metaFileName) metaFile = path
                //Handle image file
                val extension = path.fileName.extension
                if (extension == "png" || extension == "jpg") imageList.add(path)
            }

        if (imageList.isEmpty()) parserError("Missing image files")
        else this.imageList = imageList

        if (metaFile == null) parserError("Missing meta file")
        else this.metaFile = metaFile as Path
    }
}
