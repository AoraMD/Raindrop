package moe.aoramd.raindrop.view.recent

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.repository.entity.SongMeta
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel

class RecentViewModel : BarControlViewModel() {

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
}