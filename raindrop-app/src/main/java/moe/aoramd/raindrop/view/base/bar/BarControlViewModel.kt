package moe.aoramd.raindrop.view.base.bar

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.base.player.PlayerControlViewModel
import kotlin.math.roundToInt

/**
 *  music bar controllable view model
 *
 *  @author M.D.
 *  @version dev 1
 */
abstract class BarControlViewModel : PlayerControlViewModel() {

    override val listenPlayingDataChanged: Boolean = true

    private val barVisibleMutable = MutableLiveData<Boolean>()
    val barVisible: LiveData<Boolean> = barVisibleMutable

    private val barPlayingMutable = MutableLiveData<Boolean>()
    val barPlaying: LiveData<Boolean> = barPlayingMutable

    private val barProgressMutable = MutableLiveData<Int>()
    val barProgress: LiveData<Int> = barProgressMutable

    init {
        barVisibleMutable.value = false
        barPlayingMutable.value = false
        barProgressMutable.value = 0
    }

    override fun playingListChanged(songs: List<Song>) {
        super.playingListChanged(songs)
        barVisibleMutable.value = songs.isNotEmpty()
    }

    override fun playingStateChanged(state: Int) {
        super.playingStateChanged(state)
        when (state) {
            // is player playing
            PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                barPlayingMutable.value = true
            }
            else -> {
                barPlayingMutable.value = false
            }
        }
    }

    override fun playingProgressChanged(progress: Float) {
        super.playingProgressChanged(progress)
        barProgressMutable.value = progress.roundToInt()
    }

    fun barSkipToPrevious() {
        controller?.transportControls?.skipToPrevious()
    }

    fun barPlay() {
        controller?.apply {
            if (barPlaying.value == true)
                transportControls.pause()
            else
                transportControls.play()
        }
    }

    fun barSkipToNext() {
        controller?.transportControls?.skipToNext()
    }
}