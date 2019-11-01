package moe.aoramd.raindrop.player

import android.media.MediaPlayer
import android.support.v4.media.session.PlaybackStateCompat
import kotlin.math.roundToLong

/**
 *  a Player class implementation with MediaPlayer
 *
 *  @author M.D.
 *  @version dev 1
 */
class MusicPlayer : Player() {

    private val player = MediaPlayer().apply {
        setOnPreparedListener {
            playLength = duration.toLong()
            state = preparedState
            preparedListener?.invoke()
            if (autoPlayAfterPrepared) this@MusicPlayer.play()
        }

        setOnCompletionListener {
            reset()
            state = noneState
            completedListener?.invoke()
        }

        setOnBufferingUpdateListener { _, percent ->
            state = buildState(
                if (percent < 100)
                    PlaybackStateCompat.STATE_BUFFERING
                else
                    PlaybackStateCompat.STATE_PLAYING,
                currentPosition.toLong()
            )
        }
    }

    private var playLength: Long = 0L

    override var stateChangedListener: ((state: PlaybackStateCompat) -> Unit)? = null

    override var progressChangedListener: ((progress: Float) -> Unit)? = null

    override var preparedListener: (() -> Unit)? = null

    override var completedListener: (() -> Unit)? = null

    override val playProgress: Float
        get() = if (playable)
            if (playLength == 0L) 0F else (player.currentPosition.toFloat() * 100 / playLength)
        else
            0F

    override fun prepareSource(url: String) {
        reset()
        player.apply {
            setDataSource(url)
            prepareAsync()
        }
        state = connectingState
    }

    override fun pause() {
        super.pause()
        player.pause()
        state =
            buildState(PlaybackStateCompat.STATE_PAUSED, player.currentPosition.toLong())
    }

    override fun play() {
        super.play()
        if (state.state == PlaybackStateCompat.STATE_PAUSED) {
            player.start()
            state =
                buildState(PlaybackStateCompat.STATE_PLAYING, player.currentPosition.toLong())
        }
    }

    override fun reset() {
        super.reset()
        player.reset()
        state = noneState
    }

    override fun seekTo(position: Long) {
        if (playable) {
            player.seekTo(position.toInt())
            state = buildState(state.state, player.currentPosition.toLong())
        }
    }

    override fun seekToProgress(progress: Float) {
        seekTo((playLength * progress).roundToLong())
    }

    override fun release() {
        player.release()
    }
}
