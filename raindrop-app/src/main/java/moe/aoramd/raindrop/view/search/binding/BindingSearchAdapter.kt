package moe.aoramd.raindrop.view.search.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.view.search.SearchListAdapter

/**
 *  search interface data binding adapter
 *
 *  @author M.D.
 *  @version dev 1
 */
object BindingSearchAdapter {

    /*
       data list recycler view
    */

    @JvmStatic
    @BindingAdapter("searchListAdapter")
    fun setSearchListAdapter(
        recyclerView: RecyclerView,
        adapter: SearchListAdapter
    ) {
        recyclerView.adapter = adapter
    }
}