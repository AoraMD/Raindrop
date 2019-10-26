package moe.aoramd.raindrop.netease.repo

data class UrlRepo(
    val code: Int,
    val data: List<UrlInnerRepo>
) {
    companion object {
        data class UrlInnerRepo(
            val url: String?,
            val type: String?,
            val br: Int
        )
    }
}