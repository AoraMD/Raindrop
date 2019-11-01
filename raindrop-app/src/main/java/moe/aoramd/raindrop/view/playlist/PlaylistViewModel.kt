package moe.aoramd.raindrop.view.playlist

import androidx.lifecycle.*
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.service.SongMedium
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel

/**
 *  playlist interface view model
 *
 *  @author M.D.
 *  @version dev 1
 */
class PlaylistViewModel(val playlist: Playlist) : BarControlViewModel() {

    // event
    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    // view model factory
    @Suppress("UNCHECKED_CAST")
    class Factory(private val playlist: Playlist) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PlaylistViewModel(playlist) as T
    }

    // view : is list loading
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // data : songs
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    init {
        _loading.value = true
        _songs.value = listOf()

        load()
    }

    private fun load() {
        RaindropRepository.loadSongs(
            viewModelScope,
            true,
            playlist.id,
            { songs ->
                _songs.value = songs
                _loading.value = false
            },
            { errorMsg -> _event.value = errorMsg })
    }

    internal fun playPlaylist(index: Long) {
        _songs.value?.apply {
            service?.resetPlayingList(SongMedium.fromSongs(this), index)
        }
    }

    internal fun playAsNext(index: Long): Boolean {
        return _songs.value?.let {
            val result = service?.emptyList() ?: false
            service?.addSongAsNext(SongMedium.fromSong(it[index.toInt()]))
            result
        } ?: false
    }

    internal fun download(index: Long) {
        _songs.value?.let {
            RaindropRepository.downloadSong(
                viewModelScope,
                it[index.toInt()],
                success = {
                    _event.value = RaindropRepository.MSG_DOWNLOAD_SUCCESSFULLY
                },
                error = { errorMsg ->
                    _event.value = errorMsg
                })
        }
    }
}