package moe.aoramd.raindrop.view.recent.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.view.recent.RecentAdapter

object BindingRecentAdapter {

    @JvmStatic
    @BindingAdapter("recentAdapter")
    fun setMusicAdapter(
        recyclerView: RecyclerView,
        adapter: RecentAdapter
    ) {
        recyclerView.adapter = adapter
    }
}