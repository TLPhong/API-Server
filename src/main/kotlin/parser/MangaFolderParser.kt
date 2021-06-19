package tlp.media.server.komga.parser

import io.ktor.util.*
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.exception.ParserException
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.Page
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class MangaFolderParser(private val mangaFolderPath: Path) {
    private var id: String
    private val baseUrl = Constant.baseUrl
    private val metaFileName = "galleryinfo.txt"

    init {
        id = getId()
    }

    private fun parserError(message: String): Nothing = throw ParserException(message)

    fun parse(): MangaFolder {
        val relevantFiles = scanForRelevantFile()
        val imageList:List<Path> = relevantFiles.imageList.sortedWith(naturalOrder())
        val metaFile:Path = relevantFiles.metaFile

        val pages = imageList
            .mapIndexed { index, path ->
                val fileName = path.fileName
                val url = "$baseUrl/manga/$id/$fileName"
                path to Page(index, imageUrl = url)
            }
        val galleryInfo = MangaFolderMetaParser(metaFile).parse()
        return MangaFolder(id, galleryInfo, pages)
    }


    fun getId(): String {
        val fileName = mangaFolderPath.fileName!!.toString()
        val startIndex = fileName.lastIndexOf("[")
        val endIndex = fileName.lastIndexOf("]")

        return if (startIndex > 0 && endIndex > 0) {
            fileName.substring(startIndex + 1, endIndex)
        } else {
            parserError("Dir name missing ID")
        }
    }

    private fun validateFolder() {
        if (!Files.exists(mangaFolderPath)) {
            parserError("Path not exist $mangaFolderPath")
        }

        if (!Files.isDirectory(mangaFolderPath)) {
            parserError("Path is not a directory $mangaFolderPath")
        }
    }

    private data class RelevantFiles(
        val metaFile: Path,
        val imageList: List<Path>
    )

    private fun scanForRelevantFile():RelevantFiles {
        validateFolder()
        val acceptedImageExtension: List<String> = listOf("png", "jpg")
        var metaFile: Path? = null
        val imageList: MutableList<Path> = mutableListOf()

        for (path in mangaFolderPath) {
            //Handle meta file
            if (path.fileName.toString() == metaFileName) {
                metaFile = path
            }

            //Handle image file
            if (path.fileName.extension in acceptedImageExtension) {
                imageList.add(path)
            }
        }

        if (imageList.isEmpty()) parserError("Missing image files")
        metaFile = metaFile ?: parserError("Missing meta files")

        return RelevantFiles(
            imageList = imageList,
            metaFile = metaFile
        )
    }
}
