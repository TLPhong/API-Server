import org.junit.jupiter.api.*
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
        MangaFolderService.instance
    }

    @AfterAll
    fun tearDown() {
        testResources?.deleteGalleryDir()
    }

    @Test
    @DisplayName("Test parser not crash")
    fun test_parser_not_crash() {
        val pathString =
            """test_gallery/[White Island (Mashima Saki)] Fate colors V (FateGrand Order) [1861415]/galleryinfo.txt"""
        val galleryMetaPath = Paths.get(pathString)
        val profile = MangaFolderMetaParser(galleryMetaPath)
        profile.parse()
    }
}