package moe.aoramd.raindrop.view.music

import androidx.lifecycle.*
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.raindrop.manager.AccountManager
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Account
import moe.aoramd.raindrop.repository.entity.Playlist

/**
 *  music interface view model
 *
 *  @author M.D.
 *  @version dev 1
 */
class MusicViewModel : ViewModel() {

    // event
    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    // account
    internal val account: LiveData<Account> = AccountManager.accountLiveData

    // view : is list refreshing
    val refreshing = MutableLiveData<Boolean>()

    // data : playlists
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        refreshing.value = false
        _playlists.value = listOf()
    }

    fun refresh(forceRefresh: Boolean) {
        refreshing.value = true

        // load data
        RaindropRepository.loadPlaylists(
            viewModelScope,
            forceRefresh,
            AccountManager.account.id,
            success = { playlists ->
                _playlists.value = playlists
            },
            error = { errorMsg ->
                _event.value = errorMsg
            },
            complete = {
                refreshing.value = false
            }
        )
    }
}