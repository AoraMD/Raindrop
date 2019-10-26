package moe.aoramd.raindrop.repository.source

import moe.aoramd.raindrop.repository.entity.Account
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.entity.Song
import java.io.OutputStream

interface MusicSource {

    companion object {
        const val EVENT_REQUEST_ERROR = "#_Source_request_error"
        const val EVENT_NETWORK_ERROR = "#_Source_network_error"
        const val MSG_IO_ERROR = "#_Source_io_error"
    }

    val downloadPath: String

    suspend fun login(phone: Long, password: String): SourceResult<Account>

    suspend fun updateLoginState(): Boolean

    suspend fun loadPlaylists(accountId: Long): SourceResult<List<Playlist>>

    suspend fun loadSongs(playlistId: Long): SourceResult<List<Song>>

    suspend fun loadUrl(songId: Long, bitRate: Int): String

    suspend fun downloadSong(songId: Long, bitRate: Int, stream: OutputStream): String?
}