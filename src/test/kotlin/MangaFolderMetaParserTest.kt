import org.junit.jupiter.api.*
import persistence.DatabaseConfig
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.parser.MangaFolderMetaParser
import tlp.media.server.komga.service.MangaFolderService
import java.nio.file.Path
import java.nio.file.Paths
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MangaFolderMetaParserTest {

    private val workingDir: Path = Paths.get(Constant.galleryPath)
    private var testResources: TestResources? = null
    @BeforeAll
    fun setup() {
        testResources = TestResources(
            workingDir, listOf(
                ZipFileEntry("test_manga_1.zip", "123tlp")
            )
        )
        DatabaseConfig.initialize()
        MangaFolderService.instance
    }

    @AfterAll
    fun tearDown() {
        Paths.get(DatabaseConfig.databaseFilePath).toFile().delete()
        testResources?.deleteGalleryDir()
    }

    @Test
    @DisplayName("Test parser not crash")
    fun testParserNotCrash() {
        val pathString =
            """test_gallery/[White Island (Mashima Saki)] Fate colors V (FateGrand Order) [1861415]/galleryinfo.txt"""
        val galleryMetaPath = Paths.get(pathString)
        val profile = MangaFolderMetaParser(galleryMetaPath)
        profile.parse()
    }
}
