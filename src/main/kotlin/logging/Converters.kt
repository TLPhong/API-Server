package tlp.media.server.komga.logging

import tlp.media.server.komga.logging.entity.Action
import tlp.media.server.komga.logging.entity.Item
import tlp.media.server.komga.logging.entity.Resource
import tlp.media.server.komga.logging.entity.impl.GalleryImpl
import tlp.media.server.komga.logging.entity.impl.ItemImpl
import tlp.media.server.komga.logging.entity.impl.ResourceImpl
import tlp.media.server.komga.model.MangaFolder
import tlp.media.server.komga.model.Page
import java.nio.file.Path

fun List<MangaFolder>.toGallery(action: Action) = GalleryImpl(
    name = "H@H",
    count = this.size,
    action = action
)

fun MangaFolder.toResource(deletedTime: Long? = null, action: Action): Resource = ResourceImpl(
    name = meta.title,
    galleryName = "H@H",
    count = images.size,
    tags = meta.tags.map { it.toString() },
    createdTime = meta.downloadedTime,
    deletedTime = deletedTime,
    action = action
)

fun Pair<Path, Page>.toItem(resourceName: String, action: Action): Item = ItemImpl(
    index = second.index,
    name = first.fileName.toString(),
    resourceName = resourceName,
    action = action
)


