package tlp.media.server.komga.logging.entity.impl

import tlp.media.server.komga.logging.entity.Gallery

data class GalleryImpl internal constructor (
    override val name: String,
    override val count: Int
) : Gallery


