package moe.aoramd.raindrop.view.play

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.service.mode.ListLoopShuffleMode
import moe.aoramd.raindrop.service.mode.RandomShuffleMode
import moe.aoramd.raindrop.service.mode.SingleLoopShuffleMode
import moe.aoramd.raindrop.view.base.player.PlayerControlViewModel
import java.lang.Exception
import kotlin.math.roundToInt

/**
 *  music play interface view model
 *
 *  @author M.D.
 *  @version dev 1
 */
class PlayViewModel : PlayerControlViewModel() {

    // event
    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    // listen playing data change

    override val listenPlayingDataChanged: Boolean = true

    override fun playingSongChanged(song: Song, index: Int) {
        super.playingSongChanged(song, index)
        _uiColor.value = 0xff424242.toInt()
        _uiColorLight.value = 0xff6d6d6d.toInt()
        playingSong.value = song
        _playingIndex.value = index
    }

    override fun playingListChanged(songs: List<Song>) {
        super.playingListChanged(songs)
        _playingList.value = songs
    }

    override fun playingProgressChanged(progress: Float) {
        super.playingProgressChanged(progress)
        if (!seeking)
            _progress.value = progress.roundToInt()
    }

    override fun playingStateChanged(state: Int) {
        super.playingStateChanged(state)
        when (state) {
            PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                _playing.value = true
            }
            else -> {
                _playing.value = false

            }
        }
    }

    override fun playingShuffleModeChanged(mode: Int) {
        super.playingShuffleModeChanged(mode)
        shuffleMode.value = mode
    }

    override fun eventListener(event: String) {
        _event.value = event
    }

    // is music progress bar seeking
    private var seeking = false

    // view : show progress bar
    val showProgressBar = MutableLiveData<Boolean>()

    // view : show playing list
    val showPlayingList = MutableLiveData<Boolean>()

    // data : playing song
    private val playingSong = MutableLiveData<Song>()

    // view : playing song info
    val name: LiveData<String> = Transformations.map(playingSong) { it.name }
    val authors: LiveData<String> = Transformations.map(playingSong) { it.authorsName }
    val imageUrl: LiveData<String> = Transformations.map(playingSong) { it.album.coverUrl }

    // data : playing list
    private val _playingList = MutableLiveData<List<Song>>()
    val playingList: LiveData<List<Song>> = _playingList

    // data : playing index
    private val _playingIndex = MutableLiveData<Int>()
    val playingIndex: LiveData<Int> = _playingIndex

    // view : playing
    private val _playing = MutableLiveData<Boolean>()
    val playing: LiveData<Boolean> = _playing

    // view : interface palette main color
    private val _uiColor = MutableLiveData<Int>()
    val uiColor: LiveData<Int> = _uiColor

    // view : interface palette light color
    private val _uiColorLight = MutableLiveData<Int>()
    val uiColorLight: LiveData<Int> = _uiColorLight

    // data : playing progress
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    // data : shuffle mode
    private val shuffleMode = MutableLiveData<Int>()
    val shuffleModeIcon: LiveData<Int> = Transformations.map(shuffleMode) {
        when (it) {
            SingleLoopShuffleMode.tag -> R.drawable.ic_shuffle_single_loop
            RandomShuffleMode.tag -> R.drawable.ic_shuffle_random
            else -> R.drawable.ic_shuffle_list_loop
        }
    }

    init {
        showProgressBar.value = false
        showPlayingList.value = false
        playingSong.value = Song.unknown
        _playingIndex.value = -1
        _playingList.value = listOf()
        _playing.value = false
        _progress.value = 0
        _uiColor.value = 0xff424242.toInt()
        _uiColorLight.value = 0xff6d6d6d.toInt()
        shuffleMode.value = ListLoopShuffleMode.tag
    }

    // palette target
    val paletteTarget = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            bitmap?.let {
                Palette.from(it).generate { palette ->
                    _uiColor.value = palette?.vibrantSwatch?.rgb ?: 0xff424242.toInt()
                    _uiColorLight.value = palette?.lightVibrantSwatch?.rgb ?: 0xff6d6d6d.toInt()
                }
            }
        }
    }

    fun progressStartChange() {
        seeking = true
    }

    fun changedProgress(progress: Int) {
        controller?.transportControls?.sendCustomAction(
            PlayService.ACTION_SEEK_TO_PROGRESS,
            Bundle().apply {
                putFloat("progress", progress.toFloat() / 100)
            }
        )
        seeking = false
    }

    // panel click listeners

    fun changeShuffleMode() {
        controller?.transportControls?.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    }

    fun skipToPrevious() {
        controller?.transportControls?.skipToPrevious()
    }

    fun play() {
        controller?.apply {
            if (_playing.value == true)
                transportControls.pause()
            else
                transportControls.play()
        }
    }

    fun skipToNext() {
        controller?.transportControls?.skipToNext()
    }

    fun like() {
        controller?.transportControls?.sendCustomAction(
            PlayService.ACTION_LIKE,
            Bundle.EMPTY
        )
    }

    fun enableProgress() {
        showProgressBar.value = !showProgressBar.value!!
    }

    fun enablePlayingList() {
        showPlayingList.value = true
    }

    fun selectSong(index: Int) {
        controller?.transportControls?.skipToQueueItem(index.toLong())
    }
}