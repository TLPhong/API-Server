package tlp.media.server.komga.parser

import tlp.media.server.komga.model.GalleryInfo
import tlp.media.server.komga.model.Tag
import java.nio.file.Files
import java.nio.file.Path

class MangaFolderMetaParser(metaFile: Path) {
    val allLines: List<String> = Files.readAllLines(metaFile)

    fun parse(): GalleryInfo {
        val sections = mapToSection()
        return sectionsToGalleryInfo(sections)
    }

    private fun sectionsToGalleryInfo(sections: Map<String, String>): GalleryInfo {
        return GalleryInfo(
            title = sections["Title"] ?: "",
            uploadTime = sections["Upload Time"] ?: "",
            uploadBy = sections["Uploaded By"] ?: "",
            downloaded = sections["Downloaded"] ?: "",
            tags = parseTags(sections["Tags"] ?: ""),
            description = sections["Uploader's Comments"] ?: ""
        )
    }

    private fun mapToSection(): Map<String, String> {
        val contentMap = mutableMapOf(
            "Title" to "",
            "Upload Time" to "",
            "Uploaded By" to "",
            "Downloaded" to "",
            "Tags" to "",
            "Uploader's Comments" to ""
        )

        allLines
            .subList(0, 5)
            .map { string ->
                val split = string.split(":", limit = 2)
                split[0] to split[1].trim()
            }
            .filter { contentMap.containsKey(it.first) }
            .forEach {
                contentMap[it.first] = it.second
            }

        if (allLines.size > 7) {
            contentMap["Uploader's Comments"] = allLines
                .subList(8, allLines.size - 1)
                .joinToString("\n")
        }

        return contentMap
    }

    private fun parseTags(tags: String): List<Tag> {
        return tags.split(", ")
            .map {
                val split = it.split(":")
                if (split.size == 2) Tag(split[0], split[1])
                else Tag(null, split[0])
            }
    }
}
