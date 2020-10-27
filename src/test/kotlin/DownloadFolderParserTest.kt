import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import tlp.media.server.komga.parser.DownloadFolderParser
import java.nio.file.Paths
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DownloadFolderParserTest {
    @Test
    @DisplayName("Smoke test whole folder parser")
    fun test_whole_folder_parse_not_crash() {
        val folder = Paths.get("""D:\Videos\Porn\H_H\HentaiAtHome_1.6.0\download""")
        DownloadFolderParser(folder).parse()
    }
}
