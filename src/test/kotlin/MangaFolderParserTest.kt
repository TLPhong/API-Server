import org.junit.jupiter.api.*
import persistence.DatabaseConfig
import tlp.media.server.komga.parser.MangaFolderParser
import tlp.media.server.komga.model.Manga
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.service.MangaFolderService
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MangaFolderParserTest {
    private val workingDir: Path = Paths.get(Constant.galleryPath)
    private var testResources: TestResources? = null

    @BeforeAll
    fun setup() {
        testResources = TestResources(
            workingDir, listOf(
                ZipFileEntry("test_manga_1.zip", "123tlp"),
                ZipFileEntry("292_files.zip", "123tlp")
            )
        )
        DatabaseConfig.initialize()
        MangaFolderService.instance
    }

    @AfterAll
    fun tearDown() {
        testResources?.deleteGalleryDir()
        File(Constant.databaseFilePath).delete()
        File(Constant.usageLogFilePath).delete()
    }

    @Test
    @DisplayName("Test folder parser")
    fun testFolderParserBasic() {
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

    @Test
    @DisplayName("Test page indexing")
    fun testPageIndexing() {
        val pathString =
            """${Constant.galleryPath}/manga [1926482]"""
        val path = Paths.get(pathString)
        val parse = MangaFolderParser(path).parse()
        val indexedImages = parse.images

        assertEquals(292, indexedImages.size)
        assertTrue {
            var found4 = false
            var found10 = false
            for ((_, page) in indexedImages) {
                if (page.index == 4) {
                    found4 = true
                }
                if (page.index == 10) {
                    found10 = true
                }
            }
            return@assertTrue found4 && found10
        }

        for ((file, page) in indexedImages) {
            if (page.index == 4) {
                assertEquals("05.png", file.fileName.toString())
            }
            if (page.index == 10) {
                assertEquals("11.png", file.fileName.toString())
            }
        }
    }
}
