package tlp.media.server.komga.constant

import java.util.Properties

class PropertiesReader {
    var properties: Properties

    init {
        properties = Thread.currentThread().contextClassLoader
            .getResourceAsStream("application.properties")
            .use {
                val properties = Properties()
                properties.load(it)
                properties
            }
    }
}

private val reader = PropertiesReader()

object Constant {
    val port: Int = reader.properties.getProperty("port")!!.toInt()
    val host: String = reader.properties.getProperty("host")!!
    val baseApiPath: String = reader.properties.getProperty("baseApiPath")!!
    val galleryPath: String = reader.properties.getProperty("galleryPath")!!
    val baseUrl = "http://$host:$port/$baseApiPath"
}

