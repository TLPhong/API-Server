package tlp.media.server.komga.logging.entity.impl

import tlp.media.server.komga.logging.entity.Item
import tlp.media.server.komga.model.Page
import java.nio.file.Path

data class ItemImpl internal constructor(
    override val index: Int,
    override val name: String
) : Item


