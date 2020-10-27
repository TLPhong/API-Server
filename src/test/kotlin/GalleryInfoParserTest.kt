import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import tlp.media.server.komga.parser.GalleryInfoParser
import java.nio.file.Paths
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GalleryInfoParserTest {
    @Test
    @DisplayName("Test parser not crash")
    fun test_parser_not_crash() {
        val pathString =
            """D:\Videos\Porn\H_H\HentaiAtHome_1.6.0\download\Fluffy Tail Series. Renamon incumming. [1760614]\galleryinfo.txt"""

        GalleryInfoParser(Paths.get(pathString)).parse()

    }
}
