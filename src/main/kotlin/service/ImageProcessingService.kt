package tlp.media.server.komga.service

import org.imgscalr.Scalr
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


class ImageProcessingService {
    companion object {
        val instance = ImageProcessingService()
    }

    fun resized(byteArray: ByteArray, extension: String, width: Int, height: Int): ByteArray {
        return Scalr.resize(
            ImageIO.read(ByteArrayInputStream(byteArray)),
            Scalr.Method.QUALITY,
            Scalr.Mode.FIT_TO_HEIGHT,
            width,
            height
        ).let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            ImageIO.write(it, extension, byteArrayOutputStream)
            return@let byteArrayOutputStream.toByteArray()
        }
    }

}
