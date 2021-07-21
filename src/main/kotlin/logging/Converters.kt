package tlp.media.server.komga.logging

import tlp.media.server.komga.logging.entity.Item
import tlp.media.server.komga.logging.entity.Resource
import tlp.media.server.komga.logging.entity.impl.GalleryImpl
import tlp.media.server.komga.logging.entity.impl.ItemImpl
import tlp.media.server.komga.logging.entity.impl.ResourceImpl
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.Page
import java.nio.file.Path

fun List<MangaFolder>.toGallery() = GalleryImpl(
    resources = this.map { it.toResource() },
    count = this.size
)

fun MangaFolder.toResource(deletedTime: Long? = null): Resource = ResourceImpl(
    name = meta.title,
    count = images.size,
    tags = meta.tags.map { it.toString() },
    items = images.map { it.toItem() },
    createdTime = meta.downloadeTime,
    deletedTime = deletedTime
)

fun Pair<Path, Page>.toItem(): Item = ItemImpl(
    index = second.index,
    name = first.fileName.toString()
)


