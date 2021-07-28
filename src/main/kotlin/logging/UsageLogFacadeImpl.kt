package tlp.media.server.komga.logging

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import logging.UsageLogFacade
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.logging.entity.Gallery
import tlp.media.server.komga.logging.entity.Item
import tlp.media.server.komga.logging.entity.Resource
import tlp.media.server.komga.logging.entity.impl.ItemImpl
import java.io.File

class UsageLogFacadeImpl : UsageLogFacade {
    private val usageLogFile: File = File(Constant.usageLogFileName)
    private val json = Json { encodeDefaults = true }

    init {
        usageLogFile.createNewFile()
    }

    override fun galleryAddResource(gallery: Gallery, resource: Resource) {
        TODO("Not yet implemented")
    }

    override fun galleryRemoveResource(gallery: Gallery, resource: Resource) {
        TODO("Not yet implemented")
    }

    override fun resourcesBeListing(resource: List<Resource>) {
        TODO("Not yet implemented")
    }

    override fun itemsBeListing(items: List<Item>) = write(items)

    override fun itemBeServing(item: Item) = write(item)

    private fun write(target: Any) {
        val targetJson = json.encodeToString(target)
        usageLogFile.appendText("$targetJson\n", charset = Charsets.UTF_8)
    }
}
