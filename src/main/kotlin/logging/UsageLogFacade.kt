package logging

import tlp.media.server.komga.logging.entity.Gallery
import tlp.media.server.komga.logging.entity.Item
import tlp.media.server.komga.logging.entity.Resource

/*
Let there be Gallery
    IS: Collection of Resource
    CAN:
        add Resource
        remove Resource
    PROPS:
        name
        number of Resource

Let there be Resource
     IS: collection of Item
     PROPS:
        name
        number of Item
        tags
        created time (epoch)
        deleted time (epoch)
     CAN: be listing

Let there be Item
    IS: static media file
    PROPS:
        name
        index
    CAN:
        be listing
        be serving
 */
interface UsageLogFacade {
    fun galleryAddResource(gallery: Gallery, resource: Resource)
    fun galleryRemoveResource(gallery: Gallery, resource: Resource)
    fun resourcesBeListing(resource: List<Resource>)
    fun itemsBeListing(items: List<Item>)
    fun itemBeServing(item: Item)
}
