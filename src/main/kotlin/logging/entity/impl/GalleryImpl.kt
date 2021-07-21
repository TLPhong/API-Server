package tlp.media.server.komga.logging.entity.impl

import tlp.media.server.komga.logging.entity.Gallery
import tlp.media.server.komga.logging.entity.Resource
import tlp.media.server.komga.model.MangaFolder

class GalleryImpl internal constructor (
    override val resources: List<Resource>,
    override val count: Int) : Gallery


