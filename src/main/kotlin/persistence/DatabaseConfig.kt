package persistence

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import tlp.media.server.komga.constant.Constant

object DatabaseConfig {
    val databaseFilePath = Constant.usageLogFilePath
    fun initialize(logLevel: Level = Level.INFO) {
        val db = Database.connect("jdbc:sqlite:$databaseFilePath", "org.sqlite.JDBC")
        val root: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        root.level = logLevel
        transaction {
            SchemaUtils.create(MangaTable, ImageTable, TagTable, MangaTagTable)
        }
        db.useNestedTransactions = false // Inner transaction will reuse outer context
    }
}
