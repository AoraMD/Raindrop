package moe.aoramd.raindrop.netease.repo

import moe.aoramd.raindrop.repository.entity.Playlist

data class PlaylistsRepo(
    val code: Int,
    val more: Boolean,
    val playlist: List<Playlist>
)