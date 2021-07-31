package tlp.media.server.komga

import ch.qos.logback.classic.Level
import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import persistence.DatabaseConfig
import tlp.media.server.komga.constant.Constant

private fun configure() {
    DatabaseConfig.initialize(logLevel = Level.toLevel(Constant.logLevel))

}

fun main() {
    configure()
    embeddedServer(
        Netty,
        port = Constant.port,
        watchPaths = listOf("""Media/KomgaAPI"""),
        module = Application::apiModule
    ).start(wait = true)
}
