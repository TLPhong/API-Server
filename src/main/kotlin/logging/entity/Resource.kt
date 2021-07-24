package tlp.media.server.komga.logging.entity

import kotlinx.serialization.Serializable

interface Resource {
    val name: String
    val galleryName: String
    val count: Int
    val tags: List<String>
    val createdTime: Long
    val deletedTime: Long?
}
