package tlp.media.server.komga.logging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import logging.UsageLogFacade
import tlp.media.server.komga.constant.Constant
import tlp.media.server.komga.logging.entity.Item
import tlp.media.server.komga.logging.entity.Resource
import java.io.File

class UsageLogFacadeImpl : UsageLogFacade {
    private val usageLogFile: File = File(Constant.usageLogFileName)
    private val jsonMapper = jacksonObjectMapper()

    init {
        usageLogFile.createNewFile()
    }

    override fun resourceChanged(resource: Resource) {
        val targetJson = jsonMapper.writeValueAsString(resource)
        write(targetJson)
    }

    override fun resourcesBeListing(resource: List<Resource>) {
        val targetJson = resource.joinToString(separator = "\n") {
            jsonMapper.writeValueAsString(it)
        }
        write(targetJson)
    }

    override fun itemsBeListing(items: List<Item>) {
        val targetJson = items.joinToString(separator = "\n") {
            jsonMapper.writeValueAsString(it)
        }
        write(targetJson)
    }

    override fun itemBeServing(item: Item) {
        val targetJson = jsonMapper.writeValueAsString(item)
        write(targetJson)
    }

    private fun write(targetJson: String) {
        usageLogFile.appendText("$targetJson\n", charset = Charsets.UTF_8)
    }
}
