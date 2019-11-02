package moe.aoramd.raindrop.view.recent

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel

/**
 *  recent interface view model
 *
 *  @author M.D.
 *  @version dev 1
 */
class RecentViewModel : BarControlViewModel() {

    // data : songs [ paged list ]
    internal val songs: LiveData<PagedList<Song>>

    private val factory = RaindropRepository.playRecordSongsPagedList
    private val pagedListConfig = PagedList.Config.Builder()
        .setPageSize(30)
        .setInitialLoadSizeHint(30)
        .setEnablePlaceholders(true)
        .build()

    init {
        songs = LivePagedListBuilder(factory, pagedListConfig).build()
    }

    internal fun removeRecentIndex(index: Int) {
        val songId = songs.value?.get(index)?.id ?: 0
        RaindropRepository.removePlayRecord(viewModelScope, songId)
    }
}