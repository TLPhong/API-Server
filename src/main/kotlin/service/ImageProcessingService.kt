package tlp.media.server.komga.service

import io.ktor.util.*
import org.imgscalr.Scalr
import java.awt.image.BufferedImageOp
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import javax.imageio.ImageIO


class ImageProcessingService {
    companion object {
        val instance = ImageProcessingService()
    }

    fun resized(path: Path, width: Int, height:Int): ByteArray {
        val stream = path.toFile().inputStream()
        return Scalr.resize(
            ImageIO.read(stream),
            Scalr.Method.QUALITY,
            Scalr.Mode.FIT_TO_HEIGHT,
            width,
            height
        ).let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            ImageIO.write(it, path.extension, byteArrayOutputStream)
            return@let byteArrayOutputStream.toByteArray()
        }
    }

}
