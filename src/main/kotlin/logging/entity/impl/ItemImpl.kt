package tlp.media.server.komga.logging.entity.impl

import kotlinx.serialization.Serializable
import tlp.media.server.komga.logging.entity.Item

@Serializable
data class ItemImpl internal constructor(
    override val name: String,
    override val resourceName: String,
    override val index: Int
) : Item

