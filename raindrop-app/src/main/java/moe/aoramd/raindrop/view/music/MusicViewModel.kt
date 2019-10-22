package moe.aoramd.raindrop.view.music

import androidx.lifecycle.*
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.raindrop.manager.AccountManager
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Playlist

class MusicViewModel : ViewModel() {

    val updating = MutableLiveData<Boolean>()

    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        updating.value = false
        _playlists.value = listOf()
    }

    fun refresh(forceRefresh: Boolean) {
        updating.value = true
        RaindropRepository.loadPlaylists(
            viewModelScope,
            forceRefresh,
            AccountManager.account.id,
            { playlists ->
                _playlists.value = playlists
            },
            { errorMsg -> _event.value = errorMsg },
            { updating.value = false })
    }
}