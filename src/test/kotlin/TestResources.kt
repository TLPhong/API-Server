import net.lingala.zip4j.ZipFile
import java.io.Closeable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


data class ZipFileEntry(val zipFileName: String, val password: String)
class TestResources(private val galleryPath: Path, zipFiles: List<ZipFileEntry>) {
    private val testResourcePath: Path = Paths.get("src", "test", "resources")

    init {
        deleteGalleryDir()
        Files.createDirectory(galleryPath)
        Paths.get("src", "test", "resources")
        zipFiles
            .map { entry ->
                ZipFile(
                    Paths.get(testResourcePath.toString(), entry.zipFileName).toString(),
                    entry.password.toCharArray()
                )
            }
            .forEach { it.extractAll(galleryPath.toString()) }
    }

    fun deleteGalleryDir() {
        val galleryDir = File(galleryPath.toString())
        if (galleryDir.exists()) {
            galleryDir.deleteRecursively()
        }
    }
}
