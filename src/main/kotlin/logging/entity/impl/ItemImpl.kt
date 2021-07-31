package tlp.media.server.komga.logging.entity.impl

import tlp.media.server.komga.logging.entity.Action
import tlp.media.server.komga.logging.entity.Item

data class ItemImpl internal constructor(
    override val name: String,
    override val resourceName: String,
    override val index: Int,
    override val action: Action
) : Item, LoggingMetaImpl(version = 0.1f)
