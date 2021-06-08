package database

import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun initialize() {
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    }
}
