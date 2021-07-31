package tlp.media.server.komga.model

data class Paged <T> (
    val items: List<T>,
    val pageSize: Number,
    val hasNext: Boolean
){
    companion object {
        fun <T> fromAll(allItems: List<T>, pageIndex: Int, pageSize: Int): Paged<T>{
            val chunked = allItems.chunked(pageSize)
            val items: List<T> = chunked.getOrElse(pageIndex) { emptyList() }
            return Paged(
                items = items,
                pageSize = pageSize,
                hasNext = items.size == pageSize
            )
        }
    }
}
