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
        val reader = Files.newBufferedReader(propertyFilePath)
        val properties = Properties()
        properties.load(reader)
        this.properties = properties
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
    val port: Int = reader["port"]!!.toInt()
    val host: String = reader["host"]!!
    val baseApiPath: String = reader["baseApiPath"]!!
    val galleryPath: String = reader["galleryPath"]!!
    val baseUrl = "http://$host:$port/$baseApiPath"
    val logLevel = reader["logLevel"] ?: "INFO"
    val databaseFilePath = reader["databaseFilePath"]!!
    val usageLogFilePath = reader["usageLogFilePath"]!!
}

