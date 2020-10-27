import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import tlp.media.server.komga.parser.FolderParser
import tlp.media.server.komga.model.Manga
import tlp.media.server.komga.parser.Constant
import java.nio.file.Paths
import kotlin.test.assertEquals
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FolderParserTest {

    val urlPrefix = "http://192.168.86.3:8081/api"

    @Test
    @DisplayName("Test folder parser")
    fun test_folder_parser_basic() {
        val pathString =
            """D:\Videos\Porn\H_H\HentaiAtHome_1.6.0\download\Fluffy Tail Series. Renamon incumming. [1760614]"""
        val expectedDescription = """Hello everyone I´m Studio Natsume (AKA Natsumemetalsonic) and here you can see my 1º Hentai comic about Renamon and other digimons, thanks for the patronage of my subscribestarts on my Hentai Subscribestar:

ttps://subscribestar.adult/studio-natsume

Fluffy Tail series is, or should be, a series with the theme of "furry", yes yes, I know that not everyone like it, but I will be drawing (In theory) more hentai no furry, here I will be doing parodies and original stories, but for now we can find this parody of Renamon.

This chapter was drew 2 or 3 years ago to sell it in a manga event in Spain, but now I´m continue it in Subscribestar.

You can find 6 more pages uploaded there, and I hope have more soon.

Please enjoy it.

P.D: There was another gallery, yes, but I could not upload it.
"""

        val expected = Manga(
            "natsumemetalsonic",
            description = expectedDescription,
            thumbnail_url = "${Constant.baseUrl}/manga/1760614/portada_internet.jpg",
            title = "Fluffy Tail Series. Renamon incumming.",
            url = "/${Constant.baseApiPath}/manga/1760614"
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
