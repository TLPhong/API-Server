package tlp.media.server.komga.model

import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val index: Int,
    val imageUrl: String
)
