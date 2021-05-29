import org.junit.jupiter.api.*
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.parser.GalleryFolderParser
import tlp.media.server.komga.service.MangaFolderService
import java.nio.file.Path
import java.nio.file.Paths
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GalleryMangaFolderParserTest {

    private val workingDir: Path = Paths.get(Constant.galleryPath)
    private var testResources: TestResources? = null


    @BeforeAll
    fun setup() {
        testResources = TestResources(
            workingDir, listOf(
                ZipFileEntry("test_manga_1.zip", "123tlp"),
                ZipFileEntry("test_manga_2.zip", "123tlp")
            )
        )
        MangaFolderService.instance
    }

    @AfterAll
    fun tearDown() {
        testResources?.deleteGalleryDir()
    }

    @Test
    @DisplayName("Smoke test whole folder parser")
    fun test_whole_folder_parse_not_crash() {
        GalleryFolderParser(workingDir).parse()
    }
}
