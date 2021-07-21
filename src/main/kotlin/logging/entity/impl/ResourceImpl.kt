package tlp.media.server.komga.logging.entity.impl

import tlp.media.server.komga.logging.entity.Item
import tlp.media.server.komga.logging.entity.Resource
import tlp.media.server.komga.model.MangaFolder

class ResourceImpl internal constructor(
    override val name: String,
    override val count: Int,
    override val tags: List<String>,
    override val items: List<Item>,
    override val createdTime: Long,
    override val deletedTime: Long?
) : Resource
