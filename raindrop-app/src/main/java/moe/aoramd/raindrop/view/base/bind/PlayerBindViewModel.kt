package moe.aoramd.raindrop.view.base.bind

import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.aoramd.raindrop.IPlayListener
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.service.SongMedium

abstract class PlayerBindViewModel : ViewModel() {

    protected abstract val listenPlayingDataChanged: Boolean

    internal var service: IPlayService? = null

    internal var controller: MediaControllerCompat? = null
        set(value) {
            field = if (value != null) {
                value.registerCallback(controllerCallback)
                value
            } else {
                field?.unregisterCallback(controllerCallback)
                null
            }
        }

    internal val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            playingStateChanged(state)
        }
    }

    internal val playListener = object : IPlayListener.Stub() {
        override fun onPlayingSongChanged(songMedium: SongMedium, index: Int) {
            val song = SongMedium.toSong(songMedium)
            viewModelScope.launch(Dispatchers.Main) {
                playingSongChanged(song, index)
            }
        }

        override fun onPlayingListChanged(songMediums: MutableList<SongMedium>) {
            val songs = SongMedium.toSongs(songMediums)
            viewModelScope.launch(Dispatchers.Main) {
                playingListChanged(songs)
            }
        }
    }

    internal fun addPlayListenerIfNeed() {
        if (listenPlayingDataChanged) {
            try {
                service?.addPlayingListener(this.toString(), playListener)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    protected open fun playingSongChanged(song: Song, index: Int) {}

    protected open fun playingListChanged(songs: List<Song>) {}

    protected open fun playingStateChanged(state: PlaybackStateCompat?) {}
}