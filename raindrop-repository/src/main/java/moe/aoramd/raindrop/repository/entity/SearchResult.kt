package moe.aoramd.raindrop.repository.entity

data class SearchResult(
    val songs: List<Song>,
    val songCount: Int
)