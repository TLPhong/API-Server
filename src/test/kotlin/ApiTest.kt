import client.TlpRequests
import client.TlpResponseUtil
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import tlp.media.server.komga.service.MangaFolderService
import tlp.media.server.komga.apiModule
import tlp.media.server.komga.model.MangaWithChapter
import tlp.media.server.komga.model.MangasPage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTest {

    private val baseURL = "http://192.168.86.3:8081/api"
    private val requests = TlpRequests()
    private val parser = TlpResponseUtil()

    init {
        MangaFolderService.instance
    }


    @DisplayName("Create latest request")
    @ParameterizedTest
    @ValueSource(ints = [1, 2, 10, 100])
    fun test_create_latest_request(input: Int) {
        val expectedURL = "$baseURL/latest?page=$input&pageSize=20"
        val request = requests.latestMangas(input)
        assertEquals(request.method(), "GET")
        assertEquals(request.url().toString(), expectedURL)
    }

    @Test
    @DisplayName("Latest API call return OK")
    fun test_latest_api_call_ok() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/latest?page=0")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("application/json; charset=UTF-8", response.headers["content-type"])
                assertFalse(response.content.isNullOrEmpty())
            }
        }
    }

    @Test
    @DisplayName("Latest API can parse")
    fun test_latest_api_call_can_parse() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/latest?page=0")) {
                val content = response.content!!
                val mangasPage: MangasPage = parser.parseMangasPage(content)
                assertFalse(mangasPage.mangas.isEmpty())
                assertTrue(mangasPage.hasNextPage)
            }
        }
    }

    @Test
    @DisplayName("Manga API call return OK")
    fun test_manga_api_call_ok() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1069618")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("application/json; charset=UTF-8", response.headers["content-type"])
                assertFalse(response.content.isNullOrEmpty())
            }
        }
    }

    @Test
    @DisplayName("Manga API can parse")
    fun test_manga_api_can_parse() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1069618")) {
                val content = response.content!!
                val mangaWithChapter: MangaWithChapter = parser.parseManga(content)
                assertFalse(mangaWithChapter.manga.title.isEmpty())
            }
        }
    }

    @Test
    @DisplayName("Get image call return OK")
    fun test_image_api_call_ok() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1069618/18_Scan_17.png")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("image/png", response.headers["content-type"])
                assertNotNull(response.byteContent)
                assertFalse(response.byteContent!!.isEmpty())
            }
        }
    }

    @ParameterizedTest()
    @ValueSource(ints = [5, 15, 9999])
    @DisplayName("Client util test OK")
    fun test_latest_api_call_client_parser(input: Int) {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/latest?page=0&size=$input")) {
                val content = response.content!!
                val mangasPage: MangasPage = parser.parseMangasPage(content)
                if (input == 9999) {
                    assertTrue(mangasPage.mangas.size < input)
                    assertFalse(mangasPage.hasNextPage)
                } else {
                    assertEquals(input, mangasPage.mangas.size)
                    assertTrue(mangasPage.hasNextPage)
                }
            }
        }
    }

    @Test
    @DisplayName("Page list API call return OK")
    fun test_page_list_call_ok() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1069618/pages")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("application/json; charset=UTF-8", response.headers["content-type"])
                assertFalse(response.content.isNullOrEmpty())
            }
        }
    }

    @Test
    @DisplayName("Page list API can parse")
    fun test_page_list_call_parse() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1069618/pages")) {
                val content = response.content!!
                val parsePageList = parser.parsePageList(content)
                assertFalse(parsePageList.isEmpty())
            }
        }
    }
}
