package moe.aoramd.raindrop.repository

import android.annotation.SuppressLint
import android.os.Environment
import com.jakewharton.disklrucache.DiskLruCache
import kotlinx.coroutines.*
import moe.aoramd.lookinglass.log.GlassLog
import moe.aoramd.raindrop.repository.entity.Account
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.repository.model.MusicModel
import moe.aoramd.raindrop.repository.model.RaindropMusicModel
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.lookinglass.manager.ContextManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

@SuppressLint("StaticFieldLeak")
object RaindropRepository {

    const val MSG_FILE_EXIST_ERROR = "#_Repository_file_exist"
    const val MSG_FILE_DOWNLOADING_ERROR = "#_Repository_downloading_error"
    const val MSG_DOWNLOAD_SUCCESSFULLY = "#_Repository_download_successfully"

    lateinit var source: MusicSource
    private val model: MusicModel = RaindropMusicModel

    private const val cacheSize = 100L * 1024 * 1024

    private val cacheRootPath: String
            by lazy {
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState())
                    ContextManager.context.externalCacheDir!!.path
                else
                    ContextManager.context.cacheDir.path
            }

    private val downloadPath: String
            by lazy {
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState())
                    ContextManager.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.absolutePath + "/Raindrop"
                else
                    ContextManager.context.cacheDir.path + "/download"
            }

    private fun cacheFileName(song: Song): String =
        "$cacheRootPath${source.downloadPath}/${song.id}.0"

    private fun downloadingFileName(song: Song): String =
        "$downloadPath/${song.authorsName} - ${song.name}.temp"

    private fun downloadedFileName(song: Song): String =
        "$downloadPath/${song.authorsName} - ${song.name}.mp3"

    fun resourceString(resId: Int): String = ContextManager.resourceString(resId)

    fun resourceColor(resId: Int): Int = ContextManager.resourceColor(resId)

    fun login(
        scope: CoroutineScope,
        phone: Long,
        password: String,
        success: (account: Account) -> Unit,
        error: (errorMsg: String) -> Unit = {},
        complete: () -> Unit = {}
    ) = scope.launch {
        val result = source.login(phone, password)
        if (result.success) {
            val account = result.content
            success.invoke(account)
            complete.invoke()
            ContextManager.sharedPreferences("account").edit().apply {
                putLong("login", account.id)
                apply()
            }
            model.updateAccount(account)
        } else {
            error.invoke(result.errorMsg)
            complete.invoke()
        }
    }

    fun updateLoginState(
        scope: CoroutineScope,
        callback: (result: Boolean) -> Unit
    ) = scope.launch {
        callback.invoke(source.updateLoginState())
    }

    fun loadPlaylists(
        scope: CoroutineScope,
        refreshError: Boolean,
        accountId: Long,
        success: (playlists: List<Playlist>) -> Unit,
        error: (errorMsg: String) -> Unit = {},
        complete: () -> Unit = {}
    ) = scope.launch {
        val result = source.loadPlaylists(accountId)
        if (result.success) {
            val playlists = result.content
            success.invoke(playlists)
            complete.invoke()
            model.updatePlaylists(accountId, playlists)
        } else {
            error.invoke(result.errorMsg)
            if (refreshError)
                success.invoke(model.loadPlaylists(accountId))
            complete.invoke()
        }
    }

    fun loadSongs(
        scope: CoroutineScope,
        refreshError: Boolean,
        playlistId: Long,
        success: (songs: List<Song>) -> Unit,
        error: (errorMsg: String) -> Unit = {},
        complete: () -> Unit = {}
    ) = scope.launch {
        val result = source.loadSongs(playlistId)

        if (result.success) {
            val songs = result.content
            success.invoke(songs)
            complete.invoke()
            model.updateSongs(playlistId, songs)
        } else {
            error.invoke(result.errorMsg)
            if (refreshError)
                success.invoke(model.loadSongs(playlistId))
            complete.invoke()
        }
    }

    fun localAccount(
        scope: CoroutineScope,
        callback: (account: Account) -> Unit
    ) = scope.launch {
        val sharedPreferences =
            ContextManager.sharedPreferences("account")
        val accountId = sharedPreferences.getLong("login", 0)
        callback.invoke(model.loadAccount(accountId))
    }

    fun loadUrl(
        scope: CoroutineScope,
        song: Song,
        forceOnline: Boolean,
        success: (url: String) -> Unit,
        error: (errorMsg: String) -> Unit = {},
        complete: () -> Unit = {}
    ) {
        if (forceOnline) {
            loadOnlineUrl(scope, song.id, success, error, complete)
            return
        }

        scope.launch(Dispatchers.IO) {

            val downloadUrl = downloadingFileName(song)
            val cacheUrl = cacheFileName(song)

            when {
                // play download
                File(downloadUrl).exists() -> {
                    GlassLog.d("play download")
                    withContext(Dispatchers.Main) {
                        success.invoke(downloadUrl)
                        complete.invoke()
                    }
                }

                // play cache
                File(cacheUrl).exists() -> {
                    GlassLog.d("play cache")
                    withContext(Dispatchers.Main) {
                        success.invoke(cacheUrl)
                        complete.invoke()
                    }
                }

                // play online
                else -> {
                    GlassLog.d("play online")
                    loadOnlineUrl(scope, song.id, success, error, complete)
                }
            }
        }
    }

    private fun loadOnlineUrl(
        scope: CoroutineScope,
        songId: Long,
        success: (url: String) -> Unit,
        error: (errorMsg: String) -> Unit = {},
        complete: () -> Unit = {}
    ) = scope.launch(Dispatchers.IO) {
        val url = source.loadUrl(songId)
        withContext(Dispatchers.Main) {
            if (url == Tags.UNKNOWN_TAG)
                error.invoke(MusicSource.EVENT_NETWORK_ERROR)
            else
                success.invoke(url)
            complete.invoke()
        }

        // cache song
        cacheSong(scope, songId)
    }

    private fun cacheSong(
        scope: CoroutineScope,
        songId: Long,
        success: () -> Unit = {},
        error: (errorMsg: String) -> Unit = {}
    ) = scope.launch(Dispatchers.IO) {
        val cacheDirectory = File("$cacheRootPath${source.downloadPath}").apply {
            if (!exists()) mkdir()
        }
        val cache = DiskLruCache.open(cacheDirectory, 1, 1, cacheSize)
        val editor = cache.edit(songId.toString())
        editor?.apply {
            val stream = newOutputStream(0)
            val errorMsg = source.downloadSong(songId, stream)
            if (errorMsg == null) {
                commit()
                withContext(Dispatchers.Main) {
                    success.invoke()
                }
            } else {
                abort()
                withContext(Dispatchers.Main) {
                    error.invoke(errorMsg)
                }
            }
        }
        cache.flush()
    }

    fun downloadSong(
        scope: CoroutineScope,
        song: Song,
        success: () -> Unit,
        error: (errorMsg: String) -> Unit
    ) = scope.launch(Dispatchers.IO) {
        File(downloadPath).apply {
            if (!exists()) mkdir()
        }

        if (File(downloadedFileName(song)).exists()) {
            withContext(Dispatchers.Main) {
                error.invoke(MSG_FILE_EXIST_ERROR)
            }
            return@launch
        }

        val file = File(downloadingFileName(song))
        if (file.exists()) {
            withContext(Dispatchers.Main) {
                error.invoke(MSG_FILE_DOWNLOADING_ERROR)
            }
            return@launch
        }

        file.createNewFile()
        val stream = BufferedOutputStream(FileOutputStream(file))
        val errorMsg = source.downloadSong(song.id, stream)
        if (errorMsg == null) {
            file.renameTo(File(downloadedFileName(song)))
            withContext(Dispatchers.Main) {
                success.invoke()
            }
        } else
            withContext(Dispatchers.Main) {
                error.invoke(errorMsg)
            }
    }
}