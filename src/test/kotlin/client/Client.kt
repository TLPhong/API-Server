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
        throw NotImplementedError()
    }

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        throw NotImplementedError()
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
        throw NotImplementedError()
    }

    override fun searchMangaParse(response: Response): MangasPage {
        throw NotImplementedError()
    }

    override fun imageUrlParse(response: Response): String {
        throw NotImplementedError()
    }

    override fun mangaDetailsParse(response: Response): SManga {
        throw NotImplementedError()
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        throw NotImplementedError()
    }

    override fun pageListParse(response: Response): List<Page> {
        throw NotImplementedError()
    }
}
