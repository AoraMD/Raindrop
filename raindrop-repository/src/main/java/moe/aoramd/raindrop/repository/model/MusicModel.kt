package moe.aoramd.raindrop.repository.model

import moe.aoramd.raindrop.repository.entity.Account
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.entity.Song

interface MusicModel {

    suspend fun loadAccount(accountId: Long): Account

    suspend fun updateAccount(account: Account)

    suspend fun loadPlaylists(accountId: Long): List<Playlist>

    suspend fun updatePlaylists(accountId: Long, playlists: List<Playlist>)

    suspend fun loadSongs(playlistId: Long): List<Song>

    suspend fun updateSongs(playlistId: Long, songs: List<Song>)
}