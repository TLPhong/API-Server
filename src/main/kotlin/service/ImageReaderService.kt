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
        .maximumSize(150)
        .expireAfterAccess(15, TimeUnit.MINUTES)
        .build()

    fun loadImage(path: Path, width: Int?, height: Int?): ByteArray {
        val cacheKey = "${path.toAbsolutePath()}|[w-${width ?: -1}]|[h-${height ?: -1}]"
        return cache.get(cacheKey) {
            if (width == null || height == null) {
                loadImage(path)
            } else {
                loadCompressed(path, width, height)
            }
        }!!
    }

    private fun loadImage(path: Path): ByteArray {
        return Files.newInputStream(path).readBytes()
    }

    private fun loadCompressed(path: Path, width: Int, height: Int): ByteArray {
        return imageProcessingService.resized(path, width, height)
    }
}
