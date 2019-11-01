package moe.aoramd.raindrop.repository.model

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.*
import moe.aoramd.raindrop.repository.entity.*
import moe.aoramd.raindrop.repository.model.database.ModelDatabase
import moe.aoramd.raindrop.repository.model.relation.AccountPlaylistRelation
import moe.aoramd.raindrop.repository.model.relation.PlaylistSongRelation

object RaindropMusicModel : MusicModel {

    // entity
    private val accountDao by lazy {
        ModelDatabase.instance.accountDao()
    }

    private val playlistDao by lazy {
        ModelDatabase.instance.playlistDao()
    }

    private val albumDao by lazy {
        ModelDatabase.instance.albumDao()
    }

    private val authorDao by lazy {
        ModelDatabase.instance.authorDao()
    }

    private val playRecordDao by lazy {
        ModelDatabase.instance.playRecordDao()
    }

    private val songMetaDao by lazy {
        ModelDatabase.instance.songMetaDao()
    }

    // relation
    private val accountPlaylistRelationDao by lazy {
        ModelDatabase.instance.accountPlaylistRelationDao()
    }

    private val playlistSongRelationDao by lazy {
        ModelDatabase.instance.playlistSongRelationDao()
    }

    override suspend fun loadAccount(accountId: Long): Account =
        withContext(Dispatchers.IO) {
            return@withContext accountDao.query(accountId) ?: Account.offline
        }

    override suspend fun updateAccount(account: Account) =
        withContext(Dispatchers.IO) {
            accountDao.insert(account)
        }

    override suspend fun loadPlaylists(accountId: Long): List<Playlist> =
        withContext(Dispatchers.IO) {
            return@withContext accountPlaylistRelationDao.query(accountId)
        }

    override suspend fun updatePlaylists(accountId: Long, playlists: List<Playlist>) =
        withContext(Dispatchers.IO) {
            playlistDao.insertAll(playlists)
            accountPlaylistRelationDao.insertAll(
                playlists.map {
                    AccountPlaylistRelation(accountId, it.id)
                }
            )
            return@withContext
        }

    override suspend fun loadSongs(playlistId: Long): List<Song> =
        withContext(Dispatchers.IO) {
            val metas = playlistSongRelationDao.query(playlistId)

            val albumIds = metas.map { it.album }
            val albums = albumDao.queryAll(albumIds).map { it.id to it }.toMap()

            return@withContext metas.map {
                Song(
                    it.id,
                    it.name,
                    authorDao.queryAll(it.authors),
                    albums[it.album] ?: Album.unknown
                )
            }
        }

    override suspend fun updateSongs(playlistId: Long, songs: List<Song>) =
        withContext(Dispatchers.IO) {
            // insert songs
            songMetaDao.insertAll(
                songs.map {
                    SongMeta(
                        it.id,
                        it.name,
                        it.authors.map { author -> author.id },
                        it.album.id
                    )
                }
            )

            // insert relations
            playlistSongRelationDao.insertAll(
                songs.map {
                    PlaylistSongRelation(playlistId, it.id)
                }
            )

            // insert albums
            val albums = songs.map { it.album }
            albumDao.insertAll(albums)

            // insert authors
            val authors = mutableListOf<Author>()
            songs.map { authors.addAll(it.authors) }
            authorDao.insertAll(authors)
        }

    override suspend fun insertSong(song: Song) =
        withContext(Dispatchers.IO) {
            songMetaDao.insert(
                SongMeta(
                    song.id,
                    song.name,
                    song.authors.map { author -> author.id },
                    song.album.id
                )
            )
        }

    override suspend fun insertPlayRecord(playRecord: PlayRecord) =
        withContext(Dispatchers.IO) {
            playRecordDao.insert(playRecord)
        }

    override val playRecordSongsPagedList: DataSource.Factory<Int, Song>
        get() = playRecordDao.queryAll().map {
            runBlocking {
                Song(
                    it.id,
                    it.name,
                    authorDao.queryAll(it.authors),
                    albumDao.query(it.album) ?: Album.unknown
                )
            }

        }
}