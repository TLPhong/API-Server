package client

import okhttp3.Request
import okhttp3.HttpUrl
import tlp.media.server.komga.constant.Constant;

class TlpRequests {
    val host = Constant.host
    val port = Constant.port

    fun latestMangas(page: Int, pageSize: Int = 60): Request {
        val httpUrl = urlBuilder()
            .addPathSegment("latest")
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", pageSize.toString())
            .build()

        return Request.Builder()
            .url(httpUrl.url())
            .get()
            .build()
    }

    fun popularMangas(page: Int, pageSize: Int = 60): Request {
        val httpUrl = urlBuilder()
            .addPathSegment("popular")
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", pageSize.toString())
            .build()

        return Request.Builder()
            .url(httpUrl.url())
            .get()
            .build()
    }

    fun popularMangas(query: String, page: Int, pageSize: Int = 60): Request {
        val httpUrl = urlBuilder()
            .addPathSegment("search")
            .addQueryParameter("query", query)
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", pageSize.toString())
            .build()

        return Request.Builder()
            .url(httpUrl.url())
            .get()
            .build()
    }

    private fun urlBuilder(): HttpUrl.Builder {
        return HttpUrl.Builder()
            .scheme("http")
            .host(host)
            .port(port)
            .addPathSegment("api")
    }

}
