package tlp.media.server.komga.constant

object ApiPaths {
    fun mangaDetail(mangaId: String) = "/${Constant.baseApiPath}/manga/${mangaId}"
    fun pageList(mangaId: String) = "${mangaDetail(mangaId)}/pages"
}
