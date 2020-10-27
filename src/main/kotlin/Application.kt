package tlp.media.server.komga

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import tlp.media.server.komga.parser.Constant

fun main() {
    embeddedServer(
        Netty,
        port = Constant.port,
        watchPaths = listOf("""Media/KomgaAPI"""),
        module = Application::apiModule
    ).start(wait = true)
}
