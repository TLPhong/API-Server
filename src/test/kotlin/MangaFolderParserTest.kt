import org.junit.jupiter.api.*
import tlp.media.server.komga.parser.MangaFolderParser
import tlp.media.server.komga.model.Manga
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.service.MangaFolderService
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MangaFolderParserTest {
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
    @DisplayName("Test folder parser")
    fun test_folder_parser_basic() {
        val pathString =
            """${Constant.galleryPath}/[White Island (Mashima Saki)] Fate colors V (FateGrand Order) [1861415]"""
        val expectedDescription = """(C98) [White Island (マシマサキ)] Fate colors V (FateGrand Order)
https://www.melonbooks.co.jp/detail/detail.php?product_id=682657
https://www.pixiv.net/artworks/81198748

Artist Information:
マシマサキ:
Pixiv: https://www.pixiv.net/users/18403608
Twitter: https://twitter.com/mashima_saki
HP:http://turuma.blog.fc2.com/
"""

        val expected = Manga(
            "",
            description = expectedDescription,
            thumbnail_url = "${Constant.baseUrl}/manga/1861415/1.png/thumbnail",
            title = "[White Island (Mashima Saki)] Fate colors V (Fate/Grand Order)",
            url = "/${Constant.baseApiPath}/manga/1861415"
        )

        val path = Paths.get(pathString)
        val parse = MangaFolderParser(path).parse()
        val mangaParsed = Manga.fromMangaFolder(parse)
        assertAll(
            { assertEquals(expected.description, mangaParsed.description) },
            { assertEquals(expected.thumbnail_url, mangaParsed.thumbnail_url) },
            { assertEquals(expected.artist, mangaParsed.artist) },
            { assertEquals(expected.title, mangaParsed.title) },
            { assertEquals(expected.url, mangaParsed.url) }
        )
    }
}
