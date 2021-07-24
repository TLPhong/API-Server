package tlp.media.server.komga.logging.entity.impl

import tlp.media.server.komga.logging.entity.Item

data class ItemImpl internal constructor(
    override val name: String,
    override val resourceName: String,
    override val index: Int
) : Item{
    val version = "1.0"
}


