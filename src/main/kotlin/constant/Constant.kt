package tlp.media.server.komga.constant

import java.nio.file.Files
import java.util.Properties
import kotlin.io.path.Path

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
        val propertyFilePath = Path(propertyFile)
        this.properties = if (Files.exists(propertyFilePath)) {
            val reader = Files.newBufferedReader(propertyFilePath)
            val properties = Properties()
            properties.load(reader)
            properties
        } else {
            Thread.currentThread()
                .contextClassLoader
                .getResourceAsStream("application.properties")
                .use {
                    val properties = Properties()
                    properties.load(it)
                    properties
                }
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

private val reader = PropertiesReader()

object Constant {
    val port = reader["port"]!!.toInt()
    val host = reader["host"]!!
    val baseApiPath = reader["baseApiPath"]!!
    val galleryPath = reader["galleryPath"]!!
    val baseUrl = reader["baseUrl"]!!
    val logLevel = reader["logLevel"]!!
    val databaseFilePath = reader["databaseFilePath"]!!
    val usageLogFilePath = reader["usageLogFilePath"]!!
    val useCache = reader["useCache"]!!.toBoolean()
}

