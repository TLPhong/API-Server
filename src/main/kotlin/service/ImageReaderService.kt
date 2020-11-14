package tlp.media.server.komga.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.nio.file.Files
import java.nio.file.Path

class ImageReaderService private constructor() {
    companion object {
        val instance = ImageReaderService()
    }

    private val imageProcessingService by lazy { ImageProcessingService.instance }

    private val cache: Cache<String, ByteArray> = Caffeine.newBuilder()
        .maximumWeight(500 * 1024 * 1024)
        .weigher { _: String, byteArray: ByteArray ->
            return@weigher byteArray.size
        }
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
