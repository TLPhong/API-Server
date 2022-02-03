package tlp.media.server.komga.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.util.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class ImageReaderService private constructor() {
    companion object {
        val instance = ImageReaderService()
    }

    private val imageProcessingService by lazy { ImageProcessingService.instance }

    private val cache: Cache<String, ByteArray> = Caffeine.newBuilder()
        .maximumSize(20)
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build()

    fun loadImage(path: Path, resized: Boolean): ByteArray {
        return if (!resized) {
            loadImage(path)
        } else {
            loadCompressed(path)
        }
    }

    private fun loadImage(path: Path): ByteArray {
        return cache.get(path.toString()){
            Files.newInputStream(path).use { stream ->
                stream.readBytes()
            }
        }!!
    }

    private fun loadCompressed(path: Path): ByteArray {
        val byteArrayImage = loadImage(path)
        return imageProcessingService.resized(byteArrayImage, path.extension, 600, 800)
    }
}
