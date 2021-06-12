package persistence

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun initialize() {
        val db = Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        transaction { SchemaUtils.create(MangaTable, ImageTable, TagTable, MangaTagTable) }
        db.useNestedTransactions = false // Inner transaction will reuse outer context
    }
}
