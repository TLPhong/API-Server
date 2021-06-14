import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import persistence.*
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.parser.GalleryFolderParser
import tlp.media.server.komga.service.MangaFolderService
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceTest {
    private val workingDir: Path = Paths.get(Constant.galleryPath)
    private var testResources: TestResources? = null

    @BeforeAll
    fun setup() {
        testResources = TestResources(
            workingDir, listOf(
                ZipFileEntry("test_manga_1.zip", "123tlp"),
                ZipFileEntry("test_manga_2.zip", "123tlp")
            )
        )
        DatabaseConfig.initialize()
        val mangaFolders = GalleryFolderParser(workingDir).parse(true, showDetailLog = true)
        mangaFolders.forEach { MangaEntity.fromManga(it) }
    }

    @AfterAll
    fun tearDown() {
        testResources?.deleteGalleryDir()
        Paths.get(DatabaseConfig.databaseFileName).toFile().delete()
    }

    @Test
    @DisplayName("Manga count correct")
    fun test_manga_count() {
        val actualCount = transaction { MangaEntity.count() }
        assertEquals(2, actualCount)
    }


    @Test
    @DisplayName("Tag count correct")
    fun test_tag_count() {
        val actualCount = transaction { TagEntity.count() }
        assertEquals(4, actualCount)
    }

    @Test
    @DisplayName("Image count correct")
    fun test_image_count() {
        val actualCount = transaction { ImageEntity.count() }
        assertEquals(26, actualCount)
    }

    @Test
    @DisplayName("Mapping count correct")
    fun test_mapping_count() {
        val secondManga = transaction { MangaEntity["1861415"] }
        val imagesCount = transaction { secondManga.images.count() }
        val tagsCount = transaction { secondManga.tags.count() }
        assertEquals(14, imagesCount)
        assertEquals(3, tagsCount)
    }
}
