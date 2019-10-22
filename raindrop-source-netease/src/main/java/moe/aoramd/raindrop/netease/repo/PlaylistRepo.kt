package moe.aoramd.raindrop.netease.repo

import moe.aoramd.raindrop.repository.entity.Song

data class PlaylistRepo(
    val code: Int,
    val msg: String,
    val playlist: PlaylistInnerRepo
) {
    companion object {
        data class PlaylistInnerRepo(
            val tracks: List<Song>
        )
    }
}