package moe.aoramd.raindrop.view.play

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import moe.aoramd.lookinglass.manager.AisleManager
import moe.aoramd.raindrop.IPlayListener
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.service.SongMedium

class PlayViewModel : ViewModel() {

    // media component
    internal var service: IPlayService? = null

    internal var controller: MediaControllerCompat? = null

    internal val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)

            state?.apply {
                when (this.state) {
                    PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                        _playing.value = true
                        if (!isSeeking)
                            _progress.value = this.extras?.getInt("progress") ?: 0
                    }
                    else -> {
                        _playing.value = false
                    }
                }
            }
        }
    }

    internal val playListener = object : IPlayListener.Stub() {
        override fun onPlayingSongChanged(songMedium: SongMedium, index: Int) {
            AisleManager.main(Runnable {
                val song = SongMedium.toSong(songMedium)
                _uiColor.value = 0xff424242.toInt()
                _uiColorLight.value = 0xff6d6d6d.toInt()
                playingSong.value = song
                _playingIndex.value = index
            })
        }

        override fun onPlayingListChanged(songMediums: MutableList<SongMedium>) {
            _playingList.value = SongMedium.toSongs(songMediums)
        }
    }

    // variable
    private var isSeeking = false

    // live data
    val showProgressBar = MutableLiveData<Boolean>()

    val showPlayingList = MutableLiveData<Boolean>()

    private val playingSong = MutableLiveData<Song>()

    val like: LiveData<Boolean> = Transformations.map(playingSong) { it.like }

    val name: LiveData<String> = Transformations.map(playingSong) { it.name }

    val authors: LiveData<String> = Transformations.map(playingSong) { it.authorsName }

    val imageUrl: LiveData<String> = Transformations.map(playingSong) { it.album.coverUrl }

    private val _playingList = MutableLiveData<List<Song>>()
    val playingList: LiveData<List<Song>> = _playingList

    private val _playingIndex = MutableLiveData<Int>()
    val playingIndex: LiveData<Int> = _playingIndex

    private val _playing = MutableLiveData<Boolean>()
    val playing: LiveData<Boolean> = _playing

    private val _uiColor = MutableLiveData<Int>()
    val uiColor: LiveData<Int> = _uiColor

    private val _uiColorLight = MutableLiveData<Int>()
    val uiColorLight: LiveData<Int> = _uiColorLight

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    init {
        showProgressBar.value = false
        showPlayingList.value = false
        _playingList.value = listOf()
        _playingIndex.value = -1
        _playing.value = false
        playingSong.value = Song.offline
        _uiColor.value = 0xff424242.toInt()
        _uiColorLight.value = 0xff6d6d6d.toInt()
        _progress.value = 0
    }

    fun loadCoverCallback(bitmap: Bitmap?) {
        bitmap?.let {
            Palette.from(it).generate { palette ->
                _uiColor.value = palette?.vibrantSwatch?.rgb ?: 0xff424242.toInt()
                _uiColorLight.value = palette?.lightVibrantSwatch?.rgb ?: 0xff6d6d6d.toInt()
            }
        }
    }

    fun progressStartChange() {
        isSeeking = true
    }

    fun changedProgress(progress: Int) {
        val duration = controller?.playbackState?.extras?.getLong("length") ?: 0
        val position: Long = progress * duration / 100
        controller?.transportControls?.seekTo(position)
        isSeeking = false
    }

    // click listener
    fun onClickSkipToPrevious() {
        controller?.transportControls?.skipToPrevious()
    }

    fun onClickPlay() {
        controller?.apply {
            if (_playing.value == true)
                transportControls.pause()
            else
                transportControls.play()
        }
    }

    fun onClickSkipToNext() {
        controller?.transportControls?.skipToNext()
    }

    fun onClickLike() {
        controller?.transportControls?.sendCustomAction(
            PlayService.ACTION_LIKE,
            Bundle.EMPTY
        )
    }

    fun onClickEnableProgress() {
        showProgressBar.value = !showProgressBar.value!!
    }

    fun onClickEnablePlayingList() {
        showPlayingList.value = true
    }

    fun onClickPlayingList(index: Int) {
        controller?.transportControls?.skipToQueueItem(index.toLong())
    }
}