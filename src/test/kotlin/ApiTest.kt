import client.TlpRequests
import client.TlpResponseUtil
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import persistence.DatabaseConfig
import tlp.media.server.komga.service.MangaFolderService
import tlp.media.server.komga.apiModule
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.model.MangaWithChapter
import tlp.media.server.komga.model.MangasPage
import tlp.media.server.komga.service.GalleryManager
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTest {
    private var testResources: TestResources? = null

    private val baseURL = Constant.baseUrl
    private val requests = TlpRequests()
    private val parser = TlpResponseUtil()

    @BeforeAll
    fun setup() {
        testResources = TestResources(
            Paths.get(Constant.galleryPath),
            listOf(
                ZipFileEntry("test_manga_1.zip", "123tlp"),
                ZipFileEntry("test_manga_2.zip", "123tlp")
            )
        )
        DatabaseConfig.initialize()
        GalleryManager.instance.initialize()
        MangaFolderService.instance
    }


    @AfterAll
    fun tearDown() {
        File(Constant.databaseFilePath).delete()
        File(Constant.usageLogFilePath).delete()
        testResources?.deleteGalleryDir()
    }

    private fun assertJsonContentType(response: TestApplicationResponse) {
        assertEquals("application/json; charset=UTF-8", response.headers["content-type"])
    }


    @DisplayName("Create latest request")
    @ParameterizedTest
    @ValueSource(ints = [1, 2, 10, 100])
    fun testCreateLatestRequest(input: Int) {
        val pageSize = 60
        val expectedURL = "$baseURL/latest?page=$input&size=$pageSize"
        val request = requests.latestMangas(input, pageSize)
        assertEquals(request.method(), "GET")
        assertEquals(request.url().toString(), expectedURL)
    }

    @Test
    @DisplayName("Latest API call return OK")
    fun testLatestApiCallOk() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/latest?page=1")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertFalse(response.content.isNullOrEmpty())
                assertJsonContentType(response)
            }
        }
    }


    @Test
    @DisplayName("Latest API can parse")
    fun testLatestApiCallCanParse() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/latest?page=1")) {
                val content = response.content!!
                val mangasPage: MangasPage = parser.parseMangasPage(content)
                assertFalse(mangasPage.mangas.isEmpty())
                assertEquals(2, mangasPage.mangas.count())
            }
        }
    }

    @Test
    @DisplayName("Test paging correct")
    fun testLatestApiCallPagingCorrect() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/latest?page=1&size=10")) {
                val content = response.content!!
                val mangasPage: MangasPage = parser.parseMangasPage(content)

                assertFalse(mangasPage.hasNextPage)
            }
            with(handleRequest(HttpMethod.Get, "api/latest?page=1&size=1")) {
                val content = response.content!!
                val mangasPage: MangasPage = parser.parseMangasPage(content)

                assertTrue(mangasPage.hasNextPage)
            }
        }
    }


    @Test
    @DisplayName("Manga API call return OK")
    fun testMangaApiCallOk() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1861415")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertFalse(response.content.isNullOrEmpty())
                assertJsonContentType(response)
            }
        }
    }

    @Test
    @DisplayName("Manga API can parse")
    fun testMangaApiCanParse() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1861415")) {
                val content = response.content!!
                val mangaWithChapter: MangaWithChapter = parser.parseManga(content)
                assertFalse(mangaWithChapter.manga.title.isEmpty())
            }
        }
    }

    @Test
    @DisplayName("Get image call return OK")
    fun testImageApiCallOk() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1861415/7_8.png")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("image/png", response.headers["content-type"])
                assertNotNull(response.byteContent)
                assertFalse(response.byteContent!!.isEmpty())
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [5, 15, 9999])
    @DisplayName("Client util test OK")
    fun testLatestApiCallClientParser(input: Int) {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/latest?page=1&size=$input")) {
                val content = response.content!!
                val mangasPage: MangasPage = parser.parseMangasPage(content)
                if (input == 9999) {
                    assertTrue(mangasPage.mangas.size < input)
                    assertFalse(mangasPage.hasNextPage)
                } else {
                    assertEquals(2, mangasPage.mangas.size)
                    assertFalse(mangasPage.hasNextPage)
                }
            }
        }
    }

    @Test
    @DisplayName("Page list API call return OK")
    fun testPageListCallOk() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1861415/pages")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertFalse(response.content.isNullOrEmpty())
                assertJsonContentType(response)
            }
        }
    }

    @Test
    @DisplayName("Page list API can parse")
    fun testPageListCallParse() {
        withTestApplication(Application::apiModule) {
            with(handleRequest(HttpMethod.Get, "api/manga/1861415/pages")) {
                val content = response.content!!
                val parsePageList = parser.parsePageList(content)
                assertFalse(parsePageList.isEmpty())
            }
        }
    }
}
