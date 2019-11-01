package moe.aoramd.raindrop.view.recent.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.view.recent.RecentAdapter

/**
 *  recent interface data binding adapter
 *
 *  @author M.D.
 *  @version dev 1
 */
object BindingRecentAdapter {

    /*
       data list recycler view
    */

    @JvmStatic
    @BindingAdapter("recentAdapter")
    fun setMusicAdapter(
        recyclerView: RecyclerView,
        adapter: RecentAdapter
    ) {
        recyclerView.adapter = adapter
    }
}