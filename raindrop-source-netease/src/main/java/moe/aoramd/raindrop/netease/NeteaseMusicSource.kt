package moe.aoramd.raindrop.netease

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.aoramd.raindrop.netease.adapter.AccountTypeAdapter
import moe.aoramd.raindrop.netease.adapter.AlbumTypeAdapter
import moe.aoramd.raindrop.netease.adapter.PlaylistTypeAdapter
import moe.aoramd.raindrop.netease.adapter.SongTypeAdapter
import moe.aoramd.raindrop.netease.interceptor.GetCookieInterceptor
import moe.aoramd.raindrop.netease.interceptor.PutCookieInterceptor
import moe.aoramd.raindrop.netease.api.SourceApi
import moe.aoramd.raindrop.repository.Tags
import moe.aoramd.raindrop.repository.entity.*
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.raindrop.repository.source.SourceResult
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.io.OutputStream

class NeteaseMusicSource : MusicSource {

    companion object {
        private const val BASE_URL = "http://193.112.99.234:3000/"
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(Account::class.java, AccountTypeAdapter())
        .registerTypeAdapter(Album::class.java, AlbumTypeAdapter())
        .registerTypeAdapter(Playlist::class.java, PlaylistTypeAdapter())
        .registerTypeAdapter(Song::class.java, SongTypeAdapter())
        .create()

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))

    private val retrofit =
        builder.client(
            OkHttpClient.Builder()
                .addInterceptor(PutCookieInterceptor())
                .build()
        ).build()

    private val retrofitLogin =
        builder.client(
            OkHttpClient.Builder()
                .addInterceptor(GetCookieInterceptor())
                .build()
        ).build()

    private val api = retrofit.create(SourceApi::class.java)

    private val apiLogin = retrofitLogin.create(SourceApi::class.java)

    override val downloadPath = "/netease"

    override suspend fun login(phone: Long, password: String): SourceResult<Account> =
        withContext(Dispatchers.IO) {
            val result = request { apiLogin.login(phone, password) }
            if (result.success) {
                return@withContext result.content.run {
                    if (code == 200)
                        SourceResult(true, resultContent = account)
                    else
                        SourceResult(false, resultErrorMsg = MusicSource.EVENT_REQUEST_ERROR)
                }
            } else
                return@withContext SourceResult<Account>(
                    false,
                    resultErrorMsg = MusicSource.EVENT_NETWORK_ERROR
                )
        }

    override suspend fun updateLoginState(): Boolean =
        withContext(Dispatchers.IO) {
            val result = request { api.updateLoginState() }
            if (result.success)
                return@withContext result.content.code == 200
            else
                return@withContext false
        }

    override suspend fun loadPlaylists(accountId: Long): SourceResult<List<Playlist>> =
        withContext(Dispatchers.IO) {
            val result = request { api.loadPlaylists(accountId) }
            if (result.success) {
                return@withContext result.content.run {
                    if (code == 200)
                        SourceResult(true, resultContent = playlist)
                    else
                        SourceResult(false, resultErrorMsg = MusicSource.EVENT_REQUEST_ERROR)
                }
            } else
                return@withContext SourceResult<List<Playlist>>(
                    false,
                    resultErrorMsg = MusicSource.EVENT_NETWORK_ERROR
                )
        }

    override suspend fun loadSongs(playlistId: Long): SourceResult<List<Song>> =
        withContext(Dispatchers.IO) {
            val result = request { api.loadSongs(playlistId) }
            if (result.success) {
                return@withContext result.content.run {
                    if (code == 200)
                        SourceResult(true, resultContent = playlist.tracks)
                    else
                        SourceResult(false, MusicSource.EVENT_REQUEST_ERROR)
                }
            } else
                return@withContext SourceResult<List<Song>>(false, MusicSource.EVENT_NETWORK_ERROR)
        }

    override suspend fun loadUrl(songId: Long, bitRate: Int): String =
        withContext(Dispatchers.IO) {
            val result = request { api.loadUrl(songId, bitRate) }
            if (result.success) {
                return@withContext result.content.run {
                    if (code == 200) data[0].url ?: Tags.UNKNOWN_TAG
                    else Tags.UNKNOWN_TAG
                }
            } else return@withContext Tags.UNKNOWN_TAG
        }


    override suspend fun downloadSong(songId: Long, bitRate: Int, stream: OutputStream): String? =
        withContext(Dispatchers.IO) {
            try {
                val url = loadUrl(songId, bitRate)
                val request = Request.Builder().url(url).build()
                val response = OkHttpClient().newCall(request).execute()

                if (!response.isSuccessful)
                    return@withContext MusicSource.EVENT_NETWORK_ERROR

                return@withContext if (response.body != null) {
                    val inputStream = BufferedInputStream(response.body!!.byteStream())
                    val data = ByteArray(2048)
                    var length: Int
                    while (true) {
                        length = inputStream.read(data)
                        if (length == -1) break
                        stream.write(data, 0, length)
                    }
                    inputStream.close()
                    null
                } else
                    MusicSource.EVENT_REQUEST_ERROR
            } catch (e: Throwable) {
                return@withContext MusicSource.MSG_IO_ERROR
            } finally {
                stream.flush()
                stream.close()
            }
        }

    override suspend fun searchSongs(
        keywords: String,
        page: Int,
        pageSize: Int
    ): SourceResult<SearchResult> =
        withContext(Dispatchers.IO) {
            val result = request { api.searchSongs(keywords, page * pageSize) }
            if (result.success) {
                return@withContext result.content.run {
                    if (code == 200)
                        SourceResult(
                            true,
                            resultContent = SearchResult(data.songs, data.songCount)
                        )
                    else SourceResult(false, resultErrorMsg = MusicSource.EVENT_REQUEST_ERROR)
                }
            } else
                return@withContext SourceResult<SearchResult>(
                    false,
                    MusicSource.EVENT_NETWORK_ERROR
                )
        }

    /*
        Private Functions
     */

    private suspend fun <T> request(function: suspend () -> Response<T>): RequestResult<T> {
        val response: Response<T>
        try {
            response = function.invoke()
        } catch (e: IOException) {
            return RequestResult(false)
        }

        return if (response.code() != 200)
            RequestResult(false)
        else
            RequestResult(true, resultContent = response.body())
    }

    private data class RequestResult<T>(
        val success: Boolean,
        private val resultContent: T? = null
    ) {
        val content: T
            get() = resultContent
                ?: throw IllegalStateException("null value when request is unsuccessful")
    }
}