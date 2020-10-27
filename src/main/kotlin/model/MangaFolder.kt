package tlp.media.server.komga.model

import tlp.media.server.komga.ApiPaths
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MangaFolder(
    val id: String,
    val meta: GalleryInfo,
    val images: List<Pair<Path, Page>>
){
    val thumbnail: Pair<Path, Page> = images.first()
    var chapter: Chapter = createChapter()


    private fun createChapter():Chapter {
        return Chapter(
            date_upload = parseTimeString(meta.uploadTime),
            name = meta.title,
            url = ApiPaths.pageList(id)
        )
    }

    private fun parseTimeString(timeString: String): Long {
        val format = "yyyy-MM-dd HH:mm"
        val formatter = DateTimeFormatter.ofPattern(format)
        val dt = LocalDate.parse(timeString, formatter)
        return dt!!.toEpochDay()
    }
}
