package tlp.media.server.komga.exception

class MangaNotFoundException (mangaId: String): Exception("Manga not found [$mangaId]")
