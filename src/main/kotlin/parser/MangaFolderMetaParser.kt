package tlp.media.server.komga.parser

import tlp.media.server.komga.model.MangaInfo
import tlp.media.server.komga.model.Tag
import java.nio.file.Files
import java.nio.file.Path

class MangaFolderMetaParser(metaFile: Path) {
    private val allLines: List<String> = Files.readAllLines(metaFile)

    fun parse(): MangaInfo {
        val sections = mapToSection()
        return sectionsToGalleryInfo(sections)
    }

    private object MetaProps{
        val title = "Title"
        val uploadTime = "Upload Time"
        val uploadedBy = "Uploaded By"
        val downloaded = "Downloaded"
        val tags = "Tags"
        val uploaderComments = "Uploader's Comments"
    }

    private fun sectionsToGalleryInfo(sections: Map<String, String>): MangaInfo {
        return MangaInfo(
            title = sections[MetaProps.title] ?: "",
            uploadTime = sections[MetaProps.uploadTime] ?: "",
            uploadBy = sections[MetaProps.uploadedBy] ?: "",
            downloaded = sections[MetaProps.downloaded] ?: "",
            tags = parseTags(sections[MetaProps.tags] ?: ""),
            description = sections[MetaProps.uploaderComments] ?: ""
        )
    }

    private fun mapToSection(): Map<String, String> {
        val contentMap = mutableMapOf(
            MetaProps.title to "",
            MetaProps.uploadTime to "",
            MetaProps.uploadedBy to "",
            MetaProps.downloaded to "",
            MetaProps.tags to "",
            MetaProps.uploaderComments to ""
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
