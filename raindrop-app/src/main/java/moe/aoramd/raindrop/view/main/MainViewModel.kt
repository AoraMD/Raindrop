package moe.aoramd.raindrop.view.main

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.lookinglass.manager.AisleManager
import moe.aoramd.raindrop.IPlayListener
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.service.SongMedium

class MainViewModel : ViewModel() {

    companion object {
        // view event messages
        internal const val EVENT_OPEN_PLAY_INTERFACE = "#_View_Main_open_play_interface"
    }

    // media component
    internal var service: IPlayService? = null

    internal var controller: MediaControllerCompat? = null

    internal val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)

            state?.apply {
                when (this.state) {
                    PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                        _musicBarVisible.value = true
                        _musicBarPlaying.value = true
                        _musicBarProgress.value = this.extras?.getInt("progress") ?: 0
                    }
                    else -> {
                        _musicBarPlaying.value = false
                    }
                }
            }
        }
    }

    internal val playListener = object : IPlayListener.Stub() {
        override fun onPlayingSongChanged(songMedium: SongMedium, index: Int) {
            AisleManager.main(Runnable {
                val song = SongMedium.toSong(songMedium)
                _musicBarSongName.value = song.name
            })
        }

        override fun onPlayingListChanged(songMediums: MutableList<SongMedium>) {
            AisleManager.main(Runnable {
                _musicBarVisible.value = songMediums.isNotEmpty()
            })
        }
    }

    // live data
    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    private val _musicBarVisible = MutableLiveData<Boolean>()
    val musicBarVisible: LiveData<Boolean> = _musicBarVisible

    private val _musicBarPlaying = MutableLiveData<Boolean>()
    val musicBarPlaying: LiveData<Boolean> = _musicBarPlaying

    private val _musicBarProgress = MutableLiveData<Int>()
    val musicBarProgress: LiveData<Int> = _musicBarProgress

    private val _musicBarSongName = MutableLiveData<String>()
    val musicBarSongName: LiveData<String> = _musicBarSongName

    init {
        _musicBarVisible.value = false
        _musicBarPlaying.value = false
        _musicBarProgress.value = 0
        _musicBarSongName.value = "Not Playing"
    }

    // operation
    fun playerSkipToPrevious() = controller?.transportControls?.skipToPrevious()

    fun playerPlay() {
        controller?.apply {
            if (_musicBarPlaying.value == true)
                transportControls.pause()
            else
                transportControls.play()
        }
    }

    fun playerSkipToNext() = controller?.transportControls?.skipToNext()

    fun openPlayInterface() {
        _event.value = EVENT_OPEN_PLAY_INTERFACE
    }
}