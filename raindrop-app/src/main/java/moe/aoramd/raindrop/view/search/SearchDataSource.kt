package moe.aoramd.raindrop.view.search

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Song

/**
 *  search interface paging data source
 *
 *  @property scope load coroutines scope
 *  @property keywords search keywords
 *  @property pageSize page size
 *  @property eventListener load event listener
 *
 *  @author M.D.
 *  @version dev 1
 */
class SearchDataSource(
    private val scope: CoroutineScope,
    private val keywords: String,
    private val pageSize: Int,
    private val eventListener: (event: String) -> Unit
) :
    PageKeyedDataSource<Int, Song>() {

    companion object {
        private const val DATA_SIZE_NOT_INITIALIZE = -1
    }

    class Factory(
        private val scope: CoroutineScope,
        private val keywords: String,
        private val pageSize: Int,
        private val eventListener: (event: String) -> Unit
    ) : DataSource.Factory<Int, Song>() {
        override fun create(): DataSource<Int, Song> = SearchDataSource(
            scope,
            keywords,
            pageSize,
            eventListener
        )
    }

    // total data number
    private var dataSize = DATA_SIZE_NOT_INITIALIZE

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Song>
    ) {
        RaindropRepository.searchSongs(
            scope,
            keywords,
            0,
            pageSize,
            { searchResult ->
                dataSize = searchResult.songCount
                callback.onResult(searchResult.songs, null, availablePage(1))
            },
            { errorMsg -> eventListener.invoke(errorMsg) }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Song>) {
        RaindropRepository.searchSongs(
            scope, keywords, params.key, pageSize,
            { searchResult ->
                callback.onResult(searchResult.songs, availablePage(params.key + 1))
            },
            { errorMsg -> eventListener.invoke(errorMsg) }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Song>) {
        RaindropRepository.searchSongs(
            scope, keywords, params.key, pageSize,
            { searchResult ->
                callback.onResult(searchResult.songs, availablePage(params.key - 1))
            },
            { errorMsg -> eventListener.invoke(errorMsg) }
        )
    }

    private fun availablePage(page: Int): Int? {
        return when {
            page < 0 -> null
            page * pageSize >= dataSize -> null
            else -> page
        }
    }
}