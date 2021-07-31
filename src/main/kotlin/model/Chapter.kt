package tlp.media.server.komga.model

import eu.kanade.tachiyomi.source.model.SChapter

class Chapter(
    override var date_upload: Long,
    override var name: String,
    override var url: String
) : SChapter {
    override var chapter_number: Float = 0f
    override var scanlator: String? = null


}
