package tlp.media.server.komga.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class ImageReaderService private constructor() {
    companion object {
        val instance = ImageReaderService()
    }

    private val imageProcessingService by lazy { ImageProcessingService.instance }

    private val cache: Cache<String, ByteArray> = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterAccess(15, TimeUnit.MINUTES)
        .build()

    fun loadImage(path: Path, resized: Boolean): ByteArray {
        val cacheKey = "${path.toAbsolutePath()}" + if (resized) "|resized" else ""
        return cache.get(cacheKey) {
            if (!resized) {
                loadImage(path)
            } else {
                loadCompressed(path)
            }
        }!!
    }

    private fun loadImage(path: Path): ByteArray {
        return Files.newInputStream(path).use { it.readBytes() }
    }

    private fun loadCompressed(path: Path): ByteArray {
        return imageProcessingService.resized(path, 600, 800)
    }
}
