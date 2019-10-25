package moe.aoramd.raindrop.view.base.control

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.base.bind.PlayerBindViewModel

abstract class BarControlViewModel : PlayerBindViewModel() {

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

    override fun playingStateChanged(state: PlaybackStateCompat?) {
        super.playingStateChanged(state)
        state?.apply {
            when (this.state) {
                PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                    barVisibleMutable.value = true
                    barPlayingMutable.value = true
                    barProgressMutable.value = this.extras?.getInt("progress") ?: 0
                }
                else -> {
                    barPlayingMutable.value = false
                }
            }
        }
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