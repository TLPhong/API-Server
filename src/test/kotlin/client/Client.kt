package client

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.HttpSource
import okhttp3.Request
import okhttp3.Response

class TachiyomiClient : HttpSource() {
    override val lang: String = "en"
    override val supportsLatest: Boolean = true
    override val name: String = "TLP"
    override val baseUrl: String = "random shjt here"
    private val requests = TlpRequests()
    private val parser = TlpResponseUtil()


    override fun popularMangaRequest(page: Int): Request {
        TODO("Not yet implemented")
    }

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        TODO("Not yet implemented")
    }

    override fun latestUpdatesRequest(page: Int): Request {
        return requests.latestMangas(page)
    }

    override fun latestUpdatesParse(response: Response): MangasPage {
        val responseContent = response.body()?.string()?: error("Can't get latest update response")
        val parseMangasPage = parser.parseMangasPage(responseContent)
        return parseMangasPage.toTachiyomiModel()
    }

    override fun popularMangaParse(response: Response): MangasPage {
        TODO("Not yet implemented")
    }

    override fun searchMangaParse(response: Response): MangasPage {
        TODO("Not yet implemented")
    }

    override fun imageUrlParse(response: Response): String {
        TODO("Not yet implemented")
    }

    override fun mangaDetailsParse(response: Response): SManga {
        TODO("Not yet implemented")
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        TODO("Not yet implemented")
    }

    override fun pageListParse(response: Response): List<Page> {
        TODO("Not yet implemented")
    }
}
