import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import tlp.media.server.komga.parser.FolderParser
import tlp.media.server.komga.model.Manga
import tlp.media.server.komga.constant.Constant
import java.nio.file.Paths
import kotlin.test.assertEquals
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FolderParserTest {

    @Test
    @DisplayName("Test folder parser")
    fun test_folder_parser_basic() {
        val pathString =
            """D:\Videos\Porn\H_H\HentaiAtHome_1.6.0\download\(Kemoket 6) [Kemono Ekaki no Kousoku 2 (Sindoll)] Spell Magic [1069618]"""
        val expectedDescription = """Scans by Super Shanko.

Advertisement: Let me tell you something, brother! If you're in need of some nice or even big ass scans at a decent rate, then you come see Super Shanko, brother! He'll use that 600dpi resolution to ensure you've got nice looking work that'll be just as sweet 10 years from now, and you'd better believe it, brother!
"""

        val expected = Manga(
            "sindoll",
            description = expectedDescription,
            thumbnail_url = "${Constant.baseUrl}/manga/1069618/01_Scan_Cover.png/thumbnail",
            title = "(Kemoket 6) [Kemono Ekaki no Kousoku 2 (Sindoll)]  Spell Magic",
            url = "/${Constant.baseApiPath}/manga/1069618"
        )

        val path = Paths.get(pathString)
        val parse = FolderParser(path).parse()
        val mangaParsed = Manga.fromMangaFolder(parse)
        assertAll(
            { assertEquals(mangaParsed.description, expected.description) },
            { assertEquals(mangaParsed.thumbnail_url, expected.thumbnail_url) },
            { assertEquals(mangaParsed.artist, expected.artist) },
            { assertEquals(mangaParsed.title, expected.title) },
            { assertEquals(mangaParsed.url, expected.url) }
        )
    }
}
