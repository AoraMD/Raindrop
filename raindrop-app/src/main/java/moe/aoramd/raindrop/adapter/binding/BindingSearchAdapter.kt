package moe.aoramd.raindrop.adapter.binding

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.adapter.list.SearchListAdapter
import moe.aoramd.raindrop.repository.entity.Song

object BindingSearchAdapter {

    @FunctionalInterface
    interface SongClickListener {
        fun onClick(view: View, song: Song)
    }

    @JvmStatic
    @BindingAdapter("searchListAdapter")
    fun setSearchListAdapter(
        recyclerView: RecyclerView,
        adapter: SearchListAdapter
    ) {
        recyclerView.adapter = adapter
    }

//    @JvmStatic
//    @BindingAdapter("queryListener")
//    fun setQueryListener(searchView: SearchView, listener: SearchView.OnQueryTextListener) {
//        searchView.setOnQueryTextListener(listener)
//    }
}