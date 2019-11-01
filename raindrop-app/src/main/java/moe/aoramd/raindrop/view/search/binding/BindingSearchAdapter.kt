package moe.aoramd.raindrop.view.search.binding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.view.search.SearchListAdapter
import moe.aoramd.raindrop.repository.entity.Song

object BindingSearchAdapter {

    @JvmStatic
    @BindingAdapter("searchListAdapter")
    fun setSearchListAdapter(
        recyclerView: RecyclerView,
        adapter: SearchListAdapter
    ) {
        recyclerView.adapter = adapter
    }
}