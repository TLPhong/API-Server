package persistence

import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun initialize() {
        val db = Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        db.useNestedTransactions = false // Inner transaction will reuse outer context
    }
}
