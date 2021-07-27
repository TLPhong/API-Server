package tlp.media.server.komga.model

import java.text.SimpleDateFormat

data class MangaInfo(
    val title: String,
    val uploadTime: String,
    val uploadBy: String,
    val downloaded: String,
    val tags: List<Tag>,
    val description: String
) {
    companion object {
        private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    }

    val downloadedTime: Long = formatter.parse(downloaded).time

    val artist: String
        get() {
            val artists = tags
                .filter { tag -> tag.group == "artist" }
                .map { it.name }
            return if (artists.isNotEmpty()) {
                artists.first()
            } else {
                ""
            }
        }
}
