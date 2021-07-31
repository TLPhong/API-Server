package tlp.media.server.komga.logging.entity

interface Resource {
    val name: String
    val galleryName: String
    val count: Int
    val tags: List<String>
    val createdTime: Long
}
