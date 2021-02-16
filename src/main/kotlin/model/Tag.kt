package tlp.media.server.komga.model

data class Tag(
    val group: String?,
    val name: String
) {
    override fun toString(): String {
        return if (group.isNullOrEmpty()) {
            name.trim()
        } else {
            "${group.trim()}:${name.trim()}"
        }
    }
}
