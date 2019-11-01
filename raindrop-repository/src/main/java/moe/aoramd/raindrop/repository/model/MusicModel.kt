package moe.aoramd.raindrop.repository.model

import androidx.paging.DataSource
import moe.aoramd.raindrop.repository.entity.*

interface MusicModel {

    suspend fun loadAccount(accountId: Long): Account

    suspend fun updateAccount(account: Account)

    suspend fun loadPlaylists(accountId: Long): List<Playlist>

    suspend fun updatePlaylists(accountId: Long, playlists: List<Playlist>)

    suspend fun loadSongs(playlistId: Long): List<Song>

    suspend fun updateSongs(playlistId: Long, songs: List<Song>)

    suspend fun insertSong(song: Song)

    suspend fun insertPlayRecord(playRecord: PlayRecord)

    val playRecordSongsPagedList: DataSource.Factory<Int, Song>
}