package moe.aoramd.raindrop.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.service.SongMedium
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel

class SearchHostViewModel : BarControlViewModel() {

    private val _submitted = MutableLiveData<Boolean>().apply { value = false }
    val submitted: LiveData<Boolean> = _submitted

    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    internal fun submit() {
        _submitted.value = true
    }

    internal fun playAsNext(song: Song): Boolean {
        val result = service?.emptyList() ?: false
        service?.addSongAsNext(SongMedium.fromSong(song))
        return result
    }

    internal fun download(song: Song) {
        RaindropRepository.downloadSong(
            viewModelScope,
            song,
            success = {
                _event.value = RaindropRepository.MSG_DOWNLOAD_SUCCESSFULLY
            },
            error = { errorMsg ->
                _event.value = errorMsg
            }
        )
    }
}