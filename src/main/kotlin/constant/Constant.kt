package tlp.media.server.komga.constant

import java.util.Properties

/**
 * Load order. application.properties < env
 */
class PropertiesReader {
    private val propertyFile = "application.properties"

    lateinit var properties: Properties

    init {
        loadFilePropertyFile()
    }

    private fun loadFilePropertyFile() {
        properties = Thread.currentThread().contextClassLoader
            .getResourceAsStream(propertyFile)
            .use {
                val properties = Properties()
                properties.load(it)
                properties
            }
    }

    private fun loadEnvProperty(key: String): String? {
        return System.getenv(key)
    }

    private fun loadFileProperty(key: String): String? {
        return properties.getProperty(key)
    }

    operator fun get(key: String): String? {
        return this.loadEnvProperty(key) ?: this.loadFileProperty(key)
    }
}


object Constant {
    private val reader = PropertiesReader()

    val port: Int = reader["port"]!!.toInt()
    val host: String = reader["host"]!!
    val baseApiPath: String = reader["baseApiPath"]!!
    val galleryPath: String = reader["galleryPath"]!!
    val baseUrl = "http://$host:$port/$baseApiPath"
    val logLevel = reader["logLevel"] ?: "INFO"
    val databaseFilePath = reader["databaseFilePath"]!!
    val usageLogFilePath = reader["usageLogFilePath"]!!
}

