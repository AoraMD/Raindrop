package moe.aoramd.raindrop.player

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.media.session.PlaybackStateCompat

/**
 *  audio media player base class
 *
 *  @author M.D.
 *  @version dev 1
 */
abstract class Player {

    companion object {

        private const val MESSAGE_PROGRESS_UPDATE = 0xfe87879
        private const val DELAY_PROGRESS_UPDATE = 100L

        @JvmStatic
        val noneState = buildState(PlaybackStateCompat.STATE_NONE, 0)
        @JvmStatic
        protected val connectingState = buildState(PlaybackStateCompat.STATE_CONNECTING, 0)
        @JvmStatic
        protected val preparedState = buildState(PlaybackStateCompat.STATE_PAUSED, 0)

        // build playback state
        fun buildState(state: Int, position: Long, speed: Float = 1f): PlaybackStateCompat =
            PlaybackStateCompat.Builder()
                .setState(state, position, speed)
                .build()
    }

    // update playing progress while playing
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

    // player playback state
    protected var state: PlaybackStateCompat = noneState
        set(value) {
            field = value
            stateChangedListener?.invoke(value)
        }

    // check if player is playing
    val playing: Boolean
        get() = state.state == PlaybackStateCompat.STATE_PLAYING
                || state.state == PlaybackStateCompat.STATE_BUFFERING

    // check if player can start playing
    val playable: Boolean
        get() = state.state == PlaybackStateCompat.STATE_PLAYING
                || state.state == PlaybackStateCompat.STATE_PAUSED
                || state.state == PlaybackStateCompat.STATE_BUFFERING

    // whether auto play media after resource is prepared
    // IMPLEMENT IN SUBCLASS
    var autoPlayAfterPrepared: Boolean = false

    protected abstract val playProgress: Float

    // data change listeners
    abstract var stateChangedListener: ((state: PlaybackStateCompat) -> Unit)?
    abstract var progressChangedListener: ((progress: Float) -> Unit)?

    // player event listeners
    abstract var preparedListener: (() -> Unit)?
    abstract var completedListener: (() -> Unit)?

    open fun pause() {
        // STOP update playing progress
        progressUpdateHandler.removeMessages(MESSAGE_PROGRESS_UPDATE)
    }

    open fun play() {
        // START update playing progress
        progressUpdateHandler.sendMessageDelayed(Message.obtain().apply {
            what = MESSAGE_PROGRESS_UPDATE
        }, DELAY_PROGRESS_UPDATE)
    }

    open fun reset() {
        // STOP update playing progress
        progressUpdateHandler.removeMessages(MESSAGE_PROGRESS_UPDATE)
    }

    abstract fun prepareSource(url: String)

    abstract fun seekTo(position: Long)

    // progress value is between 0 ( 0% ) and 1 ( 100% )
    abstract fun seekToProgress(progress: Float)

    abstract fun release()
}