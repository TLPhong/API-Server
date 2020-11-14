import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import tlp.media.server.komga.apiModule
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.service.MangaFolderService
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageServeTest {
    init {
        MangaFolderService.instance
    }
    val testImage = "manga/1069618/18_Scan_17.png"
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
            with(handleRequest(HttpMethod.Get, "${Constant.baseApiPath}/$testImage?h=800&w=600")) {
                val content = this.response.byteContent!!
                 assertTrue {
                     originImageSize > content.size
                 }

            }
        }
    }
}
