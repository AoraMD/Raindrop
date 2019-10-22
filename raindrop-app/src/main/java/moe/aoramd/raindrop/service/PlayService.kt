package moe.aoramd.raindrop.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
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
import moe.aoramd.raindrop.view.play.PlayActivity
import kotlin.math.roundToInt

class PlayService : Service() {

    private val _tag = "PlayService"

    companion object {
        private const val INDEX_NONE = -1

        private const val HANDLE_MESSAGE_UPDATE = 0x4d7e9de9

        private const val STATE_UPDATE_DELAY = 500L

        private const val MEDIA_SESSION_TAG = "Raindrop Media Session Tag"

        private const val ACTION_SKIP_TO_PREVIOUS = "moe.aoramd.raindrop.playservice:previous"
        private const val ACTION_PLAY = "moe.aoramd.raindrop.playservice:play"
        private const val ACTION_PAUSE = "moe.aoramd.raindrop.playservice:pause"
        private const val ACTION_SKIP_TO_NEXT = "moe.aoramd.raindrop.playservice:next"
        const val ACTION_LIKE = "moe.aoramd.raindrop.playservice:like"

        const val ACTION_CLEAN_LIST = "moe.aoramd.raindrop.playservice:clean"

        private val notPlaying = Song(
            Tags.UNKNOWN_ID,
            "Not Playing",
            false,
            listOf(),
            Album(Tags.UNKNOWN_ID, Tags.UNKNOWN_TAG, Tags.UNKNOWN_TAG)
        )
    }

    // coroutines
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    // service
    private val listeners = mutableMapOf<String, IPlayListener>()

    // player
    private val player: MediaPlayer = MediaPlayer().apply {
        setOnPreparedListener {
            playingSongLength = it.duration.toLong()
            this@PlayService.play()
            updateMetadataAsync(playingList[playingIndex].album.coverUrl)
        }
        setOnCompletionListener {
            //            session.controller.transportControls.skipToNext()
            Log.d(_tag, "Play Complete")
        }
        setOnBufferingUpdateListener { _, percent ->
            buildAndSetState(
                if (percent < 100)
                    PlaybackStateCompat.STATE_BUFFERING
                else
                    PlaybackStateCompat.STATE_PLAYING
            )
        }
    }

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

    private lateinit var playbackState: PlaybackStateCompat

    private val stateUpdateHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == HANDLE_MESSAGE_UPDATE) {
                buildAndSetState(PlaybackStateCompat.STATE_PLAYING)
                this.sendMessageDelayed(Message.obtain().apply {
                    what = HANDLE_MESSAGE_UPDATE
                }, STATE_UPDATE_DELAY)
            }
        }
    }

    private val sessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            if (playbackState.state == PlaybackStateCompat.STATE_PAUSED) {
                play()
            } else if (playingList.isNotEmpty()) {
                if (playingIndex == INDEX_NONE)
                    playingIndex = 0
                prepare()
            }
        }

        override fun onPause() {
            if (playbackState.state == PlaybackStateCompat.STATE_PLAYING)
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
            if (playbackState.state == PlaybackStateCompat.STATE_PLAYING
                || playbackState.state == PlaybackStateCompat.STATE_PAUSED
                || playbackState.state == PlaybackStateCompat.STATE_BUFFERING
            ) {
                seekTo(pos.toInt())
            }

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
            }
        }
    }


    // notification
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

        cleanAndSetState()

        // initialize broadcast
        registerReceiver(notificationReceiver, IntentFilter(ACTION_SKIP_TO_PREVIOUS))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_PLAY))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_PAUSE))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_SKIP_TO_NEXT))
        registerReceiver(notificationReceiver, IntentFilter(ACTION_LIKE))

        // initialize notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                NotificationChannel(
                    channelId,
                    getString(R.string.app_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            manager.createNotificationChannel(channel)
        }

        updateMetadata(null)
        stopForeground(false)
    }

    override fun onDestroy() {

        player.release()

        // clean coroutines
        job.cancel()

        // unregister broadcast receiver
        unregisterReceiver(notificationReceiver)

        // RELEASE context
        ContextManager.releaseContext()

        super.onDestroy()
    }


    // private functions : action
    private fun prepare() {
        player.reset()
        playingSongLength = 0
        RaindropRepository.loadUrl(scope, playingList[playingIndex], false, {
            player.setDataSource(it)
            player.prepareAsync()
        })
        buildAndSetState(PlaybackStateCompat.STATE_CONNECTING, 0)
    }

    private fun play() {
        stateUpdateHandler.sendMessageDelayed(Message.obtain().apply {
            what = HANDLE_MESSAGE_UPDATE
        }, STATE_UPDATE_DELAY)

        player.start()
        buildAndSetState(PlaybackStateCompat.STATE_PLAYING)
        updateNotification()
    }

    private fun pause() {
        stateUpdateHandler.removeMessages(HANDLE_MESSAGE_UPDATE)

        player.pause()
        buildAndSetState(PlaybackStateCompat.STATE_PAUSED)
        updateNotification()
        stopForeground(false)
    }

    private fun seekTo(pos: Int) {
        player.seekTo(pos)
        buildAndSetState(playbackState.state)
        updateNotification()
    }

    private fun reset() {
        stateUpdateHandler.removeMessages(HANDLE_MESSAGE_UPDATE)

        player.reset()
        buildAndSetState(PlaybackStateCompat.STATE_NONE)
        stopForeground(false)
    }

    private fun resetPlayingList(list: List<Song>, index: Int) {
        stateUpdateHandler.removeMessages(HANDLE_MESSAGE_UPDATE)

        playingList.clear()
        playingList.addAll(list)
        playingListChanged()

        if (list.isNotEmpty() && index in playingList.indices) {
            playingIndex = index
            prepare()
        } else {
            playingIndex = INDEX_NONE
            player.reset()
            cleanAndSetState()
            updateMetadata(null)
            stopForeground(true)
        }
    }

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


    // private functions : notification
    private fun updateNotification() {
        val metadata = session.controller.metadata
        val builder = NotificationCompat.Builder(this, channelId).apply {
            setContentTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
            setContentText(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
            setSmallIcon(R.drawable.ic_icon_temp)
            setLargeIcon(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setContentIntent(
                PendingIntent.getActivity(
                    this@PlayService,
                    0,
                    Intent(this@PlayService, PlayActivity::class.java),
                    0
                )
            )

            addAction(
                createNotificationAction(
                    R.drawable.ic_skip_previous,
                    R.string.description_previous_song,
                    ACTION_SKIP_TO_PREVIOUS
                )
            )
            addAction(
                if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    createNotificationAction(
                        R.drawable.ic_pause,
                        R.string.description_play,
                        ACTION_PAUSE
                    )
                } else {
                    createNotificationAction(
                        R.drawable.ic_play,
                        R.string.description_play,
                        ACTION_PLAY
                    )
                }
            )
            addAction(
                createNotificationAction(
                    R.drawable.ic_skip_next,
                    R.string.description_next_song,
                    ACTION_SKIP_TO_NEXT
                )
            )
            addAction(
                createNotificationAction(
                    when {
                        playingIndex == INDEX_NONE -> R.drawable.ic_favorite_border
                        playingList[playingIndex].like -> R.drawable.ic_favorite
                        else -> R.drawable.ic_favorite_border
                    },
                    R.string.description_like,
                    ACTION_LIKE
                )
            )

            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@PlayService,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(session.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@PlayService,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )

        }

        startForeground(1, builder.build())
    }

    private fun createNotificationAction(
        icon: Int,
        description: Int,
        pendingIntentAction: String
    ): NotificationCompat.Action =
        NotificationCompat.Action(
            icon,
            getString(description),
            PendingIntent.getBroadcast(this, 0, Intent(pendingIntentAction), 0)
        )


    // private functions : playback state
    private fun buildAndSetState(
        state: Int,
        position: Long = player.currentPosition.toLong()
    ) {
        playbackState = PlaybackStateCompat.Builder()
            .setState(state, position, 1f)
            .setExtras(Bundle().apply {
                if (playbackState.state == PlaybackStateCompat.STATE_PLAYING
                    || playbackState.state == PlaybackStateCompat.STATE_PAUSED
                    || playbackState.state == PlaybackStateCompat.STATE_BUFFERING
                ) {
                    putInt(
                        "progress",
                        if (playingSongLength == 0L)
                            0
                        else
                            (player.currentPosition.toFloat() * 100 / playingSongLength).roundToInt()
                    )
                    putLong(
                        "length", playingSongLength
                    )
                    putBoolean(
                        "like",
                        playingList[playingIndex].like
                    )
                } else {
                    putInt("progress", 0)
                    putLong("length", 0)
                    putBoolean("like", false)
                }
            })
            .build()
        session.setPlaybackState(playbackState)
    }

    private fun cleanAndSetState() {
        playbackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
            .setExtras(Bundle().apply {
                putInt("progress", 0)
                putBoolean("like", false)
            })
            .build()
        session.setPlaybackState(playbackState)
    }


    // private functions : metadata
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