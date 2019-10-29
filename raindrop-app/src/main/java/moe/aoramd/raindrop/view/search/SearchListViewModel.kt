package moe.aoramd.raindrop.view.search

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import moe.aoramd.lookinglass.lifecycle.EventLiveData

class SearchListViewModel(keywords: String) : ViewModel() {

    companion object {
        private const val SEARCH_PAGE_SIZE = 30

        private val pagingConfig = PagedList.Config.Builder().run {
            setPageSize(SEARCH_PAGE_SIZE)
            setInitialLoadSizeHint(SEARCH_PAGE_SIZE)
            setEnablePlaceholders(false)
            build()
        }
    }

    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    val songs = LivePagedListBuilder(
        SearchDataSource.Factory(viewModelScope, keywords, SEARCH_PAGE_SIZE) { _event.value = it },
        pagingConfig
    ).build()

    @Suppress("UNCHECKED_CAST")
    class Factory(private val keywords: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SearchListViewModel(keywords) as T
    }
}