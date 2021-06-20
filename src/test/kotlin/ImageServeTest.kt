import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import persistence.DatabaseConfig
import tlp.media.server.komga.apiModule
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.service.GalleryManager
import tlp.media.server.komga.service.MangaFolderService
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageServeTest {
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
        GalleryManager.instance.initialize(waitDbSync = true)
        MangaFolderService.instance
    }

    @AfterAll
    fun tearDown() {
        Paths.get(DatabaseConfig.databaseFileName).toFile().delete()
        testResources?.deleteGalleryDir()
    }

    val testImage = "manga/1861415/15_16.png"
    val contentType = ContentType.Image.PNG

    @Test
    @DisplayName("Serve image, check status only")
    fun  test_serve_1_image_status_ok(){
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "${Constant.baseApiPath}/$testImage")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(contentType.toString(), response.headers["Content-type"])
            }
        }
    }

    @Test
    @DisplayName("Serve image")
    fun  test_serve_1_image(){
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "${Constant.baseApiPath}/$testImage")) {
               val content = this.response.byteContent!!
                assertFalse { content.isEmpty() }
            }
        }
    }

    @Test
    @DisplayName("Serve image resized")
    fun  test_serve_image_resized(){
        var originImageSize: Int = -1
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "${Constant.baseApiPath}/$testImage")) {
                val content = this.response.byteContent!!
                originImageSize = content.size
            }
        }
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "${Constant.baseApiPath}/$testImage/thumbnail")) {
                val content = this.response.byteContent!!
                 assertTrue {
                     originImageSize > content.size
                 }

            }
        }
    }
}
