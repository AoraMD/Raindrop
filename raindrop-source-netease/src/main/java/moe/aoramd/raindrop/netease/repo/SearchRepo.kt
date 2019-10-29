package moe.aoramd.raindrop.netease.repo

import com.google.gson.annotations.SerializedName
import moe.aoramd.raindrop.repository.entity.Song

data class SearchRepo(
    val code: Int,
    @SerializedName("result")
    val data: SearchInnerRepo
) {
    companion object {
        data class SearchInnerRepo(
            val songs: List<Song>,
            val songCount: Int
        )
    }
}