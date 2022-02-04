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

object DatabaseConfig{
    private var db: Database? = null
    fun initialize(logLevel: Level = Level.INFO) {
        // Logger
        val root: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        root.level = logLevel
        // Db connection init
        val db = if(Constant.databaseInMemory){
            Database.connect("jdbc:sqlite:${Constant.databaseFilePath}", "org.sqlite.JDBC")
        }else{
            Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", "org.sqlite.JDBC")
        }
        TransactionManager.manager.defaultIsolationLevel =  Connection.TRANSACTION_SERIALIZABLE
        db.useNestedTransactions = false // Inner transaction will reuse outer context
        // Db Init Schema
        transaction {
            SchemaUtils.create(MangaTable, ImageTable, TagTable, MangaTagTable)
        }
        this.db = db
    }
}
