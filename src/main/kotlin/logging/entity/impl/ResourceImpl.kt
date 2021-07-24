package tlp.media.server.komga.logging.entity.impl

import kotlinx.serialization.Serializable
import tlp.media.server.komga.logging.entity.Resource

@Serializable
data class ResourceImpl internal constructor(
    override val name: String,
    override val galleryName: String,
    override val count: Int,
    override val tags: List<String>,
    override val createdTime: Long,
    override val deletedTime: Long?
) : Resource
