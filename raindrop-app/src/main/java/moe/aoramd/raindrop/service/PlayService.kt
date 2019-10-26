package moe.aoramd.raindrop.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import moe.aoramd.raindrop.IPlayListener
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.Tags
import moe.aoramd.raindrop.repository.entity.Album
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.lookinglass.manager.ContextManager
import moe.aoramd.raindrop.manager.NotifyManager
import moe.aoramd.raindrop.player.MusicPlayer
import moe.aoramd.raindrop.player.Player

class PlayService : Service() {

    companion object {
        private const val INDEX_NONE = -1

        private const val MEDIA_SESSION_TAG = "Raindrop Media Session Tag"

        const val ACTION_SKIP_TO_PREVIOUS = "moe.aoramd.raindrop.playservice:previous"
        const val ACTION_PLAY = "moe.aoramd.raindrop.playservice:play"
        const val ACTION_PAUSE = "moe.aoramd.raindrop.playservice:pause"
        const val ACTION_SKIP_TO_NEXT = "moe.aoramd.raindrop.playservice:next"
        const val ACTION_LIKE = "moe.aoramd.raindrop.playservice:like"
        const val ACTION_SEEK_TO_PROGRESS = "moe.aoramd.raindrop.playservice:seek"

        const val ACTION_CLEAN_LIST = "moe.aoramd.raindrop.playservice:clean"

        private val notPlaying = Song(
            Tags.UNKNOWN_ID,
            "Not Playing",
            listOf(),
            Album(Tags.UNKNOWN_ID, Tags.UNKNOWN_TAG, Tags.UNKNOWN_TAG)
        )
    }

    // coroutines
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    // service
    private val listeners = mutableMapOf<String, IPlayListener>()

    private val musicPlayer: Player = MusicPlayer().apply {
        autoPlayAfterPrepared = true
        stateChangedListener = {
            playingState = it
            playingStateChanged()
            session.setPlaybackState(it)
            updateNotification()
        }
        progressChangedListener = { playingProgressChanged(it) }
        preparedListener = { updateMetadataAsync(playingList[playingIndex].album.coverUrl) }
    }

    private var playingState: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
        .build()

    private val playingList = mutableListOf<Song>()

    private var playingIndex = INDEX_NONE
        set(value) {
            if (field != value) {
                field = value
                playingSongChanged()
            }
        }

    private var playingSongLength: Long = 0


    // media session
    private lateinit var session: MediaSessionCompat

    // override functions
    override fun onBind(intent: Intent?): IBinder? = object : IPlayService.Stub() {
        override fun sessionToken(): MediaSessionCompat.Token = session.sessionToken

        override fun playingSong(): SongMedium =
            SongMedium.fromSong(
                if (playingIndex != INDEX_NONE)
                    playingList[INDEX_NONE]
                else
                    notPlaying
            )

        override fun addSong(songMedium: SongMedium) {
            playingList.add(SongMedium.toSong(songMedium))
            playingListChanged()
        }

        override fun addSongAsNext(songMedium: SongMedium) {
            val play = playingList.isEmpty()
            playingList.add(SongMedium.toSong(songMedium))
            playingListChanged()
            if (play) session.controller.transportControls.skipToQueueItem(0)
        }

        override fun resetPlayingList(songMediums: List<SongMedium>, index: Long) =
            resetPlayingList(SongMedium.toSongs(songMediums), index.toInt())

        override fun addPlayingListener(tag: String, listener: IPlayListener) {
            listeners[tag] = listener
            playingSongChanged(listener)
            playingListChanged(listener)
            playingStateChanged(listener)
        }

        override fun removePlayingListener(tag: String) {
            if (listeners.containsKey(tag))
                listeners.remove(tag)
        }

        override fun emptyList(): Boolean = playingList.isEmpty()
    }

    override fun onCreate() {
        super.onCreate()

        // register context
        ContextManager.registerContext(this)

        // initialize media session

        session = MediaSessionCompat(this, MEDIA_SESSION_TAG)

        session.apply {
            setCallback(sessionCallback)
            setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
            isActive = true
            controller.registerCallback(object : MediaControllerCompat.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                    updateNotification()
                }
            })
        }

        // initialize broadcast
        registerReceiver(notificationReceiver, IntentFilter(ACTION_SKIP_TO_PREVIOUS))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_PLAY))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_PAUSE))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_SKIP_TO_NEXT))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_LIKE))

        // initialize notification
        NotifyManager.registerNotificationChannel(channelId)

        updateMetadata(null)
        stopForeground(false)
    }

    override fun onDestroy() {

        musicPlayer.release()

        // clean coroutines
        job.cancel()

        // unregister broadcast receiver
        unregisterReceiver(notificationReceiver)

        // RELEASE context
        ContextManager.releaseContext()

        super.onDestroy()
    }


    /*
        action
     */
    private val sessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            if (musicPlayer.playable) {
                play()
            } else if (playingList.isNotEmpty()) {
                if (playingIndex == INDEX_NONE)
                    playingIndex = 0
                prepare()
            }
        }

        override fun onPause() {
            pause()
        }

        override fun onSkipToPrevious() {
            if (playingList.isNotEmpty()) {
                playingIndex =
                    when (playingIndex) {
                        INDEX_NONE -> 0
                        0 -> playingList.size - 1
                        else -> playingIndex - 1
                    }
                prepare()
            }
//            // todo other mode
        }

        override fun onSkipToNext() {
            if (playingList.isNotEmpty()) {
                playingIndex =
                    when (playingIndex) {
                        INDEX_NONE -> 0
                        playingList.size - 1 -> 0
                        else -> playingIndex + 1
                    }
                prepare()
            }
//            // todo other mode
        }

        override fun onSkipToQueueItem(id: Long) {
            if (playingList.isNotEmpty() && id in 0 until playingList.size && playingIndex != id.toInt()) {
                playingIndex = id.toInt()
                prepare()
            }
        }

        override fun onSeekTo(pos: Long) {
            seekTo(pos)
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            when (action) {
                ACTION_LIKE -> {
                    // todo not implement
                    updateNotification()
                }
                ACTION_CLEAN_LIST -> {
                    resetPlayingList(listOf(), INDEX_NONE)
                }
                ACTION_SEEK_TO_PROGRESS -> {
                    extras?.apply {
                        val progress = getFloat("progress", -1f)
                        if (progress != -1f)
                            musicPlayer.seekToProgress(progress)
                    }
                }
            }
        }
    }

    private fun prepare() {
        playingSongLength = 0
        RaindropRepository.loadUrl(
            scope, playingList[playingIndex],
            forceOnline = false,
            success = {
            if (it != Tags.UNKNOWN_TAG)
                musicPlayer.prepareSource(it)
            else TODO()
        })
    }

    private fun play() {
        musicPlayer.play()
        updateNotification()
    }

    private fun pause() {
        musicPlayer.pause()
        updateNotification()
        stopForeground(false)
    }

    private fun seekTo(position: Long) {
        musicPlayer.seekTo(position)
        updateNotification()
    }

    private fun resetPlayingList(list: List<Song>, index: Int) {
        musicPlayer.reset()
        playingList.clear()
        playingList.addAll(list)
        playingListChanged()

        if (list.isNotEmpty() && index in playingList.indices) {
            playingIndex = index
            prepare()
        } else {
            playingIndex = INDEX_NONE
            musicPlayer.reset()
            updateMetadata(null)
            stopForeground(true)
        }
    }

    /*
        resource change
     */
    private fun playingSongChanged() {
        val song = if (playingIndex == INDEX_NONE) notPlaying else playingList[playingIndex]
        for (pair in listeners)
            pair.value.onPlayingSongChanged(SongMedium.fromSong(song), playingIndex)
    }

    private fun playingSongChanged(listener: IPlayListener) {
        val song = if (playingIndex == INDEX_NONE) notPlaying else playingList[playingIndex]
        listener.onPlayingSongChanged(SongMedium.fromSong(song), playingIndex)
    }

    private fun playingListChanged() {
        val songMediums = SongMedium.fromSongs(playingList)
        for (pair in listeners)
            pair.value.onPlayingListChanged(songMediums)
    }

    private fun playingListChanged(listener: IPlayListener) {
        val songMediums = SongMedium.fromSongs(playingList)
        listener.onPlayingListChanged(songMediums)
    }

    private fun playingStateChanged() {
        for (pair in listeners)
            pair.value.onPlayingStateChanged(playingState.state)
    }

    private fun playingStateChanged(listener: IPlayListener) {
        listener.onPlayingStateChanged(playingState.state)
    }

    private fun playingProgressChanged(progress: Float) {
        for (pair in listeners)
            pair.value.onPlayingProgressChanged(progress)
    }

    /*
        notification
     */
    private val channelId by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            "moe.aoramd.raindrop:notification"
        else
            ""
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                when (action) {
                    ACTION_SKIP_TO_PREVIOUS ->
                        session.controller.transportControls.skipToPrevious()
                    ACTION_PLAY ->
                        session.controller.transportControls.play()
                    ACTION_PAUSE ->
                        session.controller.transportControls.pause()
                    ACTION_SKIP_TO_NEXT ->
                        session.controller.transportControls.skipToNext()
                    else ->
                        session.controller.transportControls.sendCustomAction(
                            action, Bundle.EMPTY
                        )
                }
            }
        }
    }

    private fun updateNotification() {
        val notification = NotifyManager.mediaStyleNotification(
            this, channelId, session,
            musicPlayer.playing,
            false
        )
        startForeground(1, notification)
    }

    /*
        metadata
     */
    private fun updateMetadataAsync(url: String) {
        updateMetadata(null)
        Glide.with(this)
            .asBitmap()
            .load(url)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean = false

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    updateMetadata(resource)
                    return false
                }
            })
            .submit()
    }

    private fun updateMetadata(cover: Bitmap?) {
        val builder = if (playingIndex != INDEX_NONE) {
            MediaMetadataCompat.Builder().apply {
                val song = playingList[playingIndex]
                putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.name)
                putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.authorsName)
                putBitmap(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                    cover ?: BitmapFactory.decodeResource(resources, R.drawable.img_placeholder)
                )
            }
        } else {
            MediaMetadataCompat.Builder().apply {
                putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Not Playing")
                putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "Not Playing")
                putBitmap(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                    BitmapFactory.decodeResource(resources, R.drawable.img_placeholder)
                )
            }
        }
        session.setMetadata(builder.build())
    }
}