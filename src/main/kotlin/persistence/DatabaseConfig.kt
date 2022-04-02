package persistence

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import tlp.media.server.komga.constant.Constant
import java.sql.Connection

object DatabaseConfig {
    fun initialize(logLevel: Level = Level.INFO) {
        // Logger
        val root: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        root.level = logLevel
        // Db connection init
        val db = Database.connect("jdbc:sqlite:${Constant.databaseFilePath}", "org.sqlite.JDBC")
        // Inner transaction will reuse outer context
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED

        // Db Init Schema
        transaction(db) {
            SchemaUtils.create(MangaTable, ImageTable, TagTable, MangaTagTable)
        }
    }
}
