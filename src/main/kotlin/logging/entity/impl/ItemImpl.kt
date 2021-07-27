package tlp.media.server.komga.logging.entity.impl

import kotlinx.serialization.Serializable
import tlp.media.server.komga.logging.entity.Item
import java.text.SimpleDateFormat
import java.util.*

private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS")

@Serializable
data class ItemImpl internal constructor(
    override val name: String,
    override val resourceName: String,
    override val index: Int,
    val version: Float = 0.1f,
    val logTime: String = formatter.format(Date())
) : Item
