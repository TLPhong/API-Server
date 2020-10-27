package tlp.media.server.komga

import tlp.media.server.komga.parser.Constant

object ApiPaths {
    fun mangaDetail(mangaId: String) = "/${Constant.baseApiPath}/manga/${mangaId}"
    fun pageList(mangaId: String) = "${mangaDetail(mangaId)}/pages"
}
