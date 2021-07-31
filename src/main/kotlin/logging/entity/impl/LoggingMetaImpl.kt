package tlp.media.server.komga.logging.entity.impl

import tlp.media.server.komga.logging.entity.LoggingMeta
import java.text.SimpleDateFormat
import java.util.*


private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

abstract class LoggingMetaImpl internal constructor(
    override val version: Float,
    override val logTime: String = formatter.format(Date())
) : LoggingMeta
