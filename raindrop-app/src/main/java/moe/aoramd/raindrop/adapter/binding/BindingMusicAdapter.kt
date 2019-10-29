package moe.aoramd.raindrop.adapter.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.adapter.list.MusicAdapter
import moe.aoramd.raindrop.repository.entity.Playlist

object BindingMusicAdapter {

    @JvmStatic
    @BindingAdapter("musicAdapter")
    fun setMusicAdapter(
        recyclerView: RecyclerView,
        adapter: MusicAdapter
    ) {
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("playlists")
    fun setPlaylists(recyclerView: RecyclerView, playlists: List<Playlist>) {
        val adapter = recyclerView.adapter as MusicAdapter
        adapter.data = playlists
        adapter.notifyDataSetChanged()
    }
}