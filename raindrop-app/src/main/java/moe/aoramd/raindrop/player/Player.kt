package moe.aoramd.raindrop.player

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.media.session.PlaybackStateCompat

abstract class Player {

    companion object {
        private const val MESSAGE_PROGRESS_UPDATE = 0xfe87879
        private const val DELAY_PROGRESS_UPDATE = 100L
    }

    protected val noneState = buildState(PlaybackStateCompat.STATE_NONE, 0)
    protected val connectingState = buildState(PlaybackStateCompat.STATE_CONNECTING, 0)
    protected val preparedState = buildState(PlaybackStateCompat.STATE_PAUSED, 0)

    private val progressUpdateHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MESSAGE_PROGRESS_UPDATE) {
                progressChangedListener?.invoke(playProgress)
                this.sendMessageDelayed(Message.obtain().apply {
                    what = MESSAGE_PROGRESS_UPDATE
                }, DELAY_PROGRESS_UPDATE)
            }
        }
    }

    protected var state: PlaybackStateCompat = noneState
        set(value) {
            field = value
            stateChangedListener?.invoke(value)
        }

    val playing: Boolean
        get() = state.state == PlaybackStateCompat.STATE_PLAYING
                || state.state == PlaybackStateCompat.STATE_BUFFERING

    val playable: Boolean
        get() = state.state == PlaybackStateCompat.STATE_PLAYING
                || state.state == PlaybackStateCompat.STATE_PAUSED
                || state.state == PlaybackStateCompat.STATE_BUFFERING

    var autoPlayAfterPrepared: Boolean = false

    protected abstract var playProgress: Float

    abstract var stateChangedListener: ((state: PlaybackStateCompat) -> Unit)?

    abstract var progressChangedListener: ((progress: Float) -> Unit)?
    abstract var preparedListener: (() -> Unit)?

    abstract var completedListener: (() -> Unit)?

    abstract fun prepareSource(url: String)

    open fun pause() {
        progressUpdateHandler.removeMessages(MESSAGE_PROGRESS_UPDATE)
    }

    open fun play() {
        progressUpdateHandler.sendMessageDelayed(Message.obtain().apply {
            what = MESSAGE_PROGRESS_UPDATE
        }, DELAY_PROGRESS_UPDATE)
    }

    open fun reset() {
        progressUpdateHandler.removeMessages(MESSAGE_PROGRESS_UPDATE)
    }

    abstract fun seekTo(position: Long)

    abstract fun seekToProgress(progress: Float)

    abstract fun release()

    fun buildState(state: Int, position: Long, speed: Float = 1f): PlaybackStateCompat =
        PlaybackStateCompat.Builder()
            .setState(state, position, speed)
            .build()
}