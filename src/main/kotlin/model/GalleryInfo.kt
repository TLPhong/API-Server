package tlp.media.server.komga.model

data class GalleryInfo(
    val title: String,
    val uploadTime: String,
    val uploadBy: String,
    val downloaded: String,
    val tags: List<Tag>,
    val description: String
) {
    fun getArtist(): String {
        val artists = tags
            .filter { tag -> tag.group == "artist" }
            .map { it.name }
        return if (artists.isNotEmpty()) {
            artists.first()
        } else {
            ""
        }
    }

    fun getTagString(): String {
        return tags.map { it.toString() }.joinToString ( ", " )
    }
}
