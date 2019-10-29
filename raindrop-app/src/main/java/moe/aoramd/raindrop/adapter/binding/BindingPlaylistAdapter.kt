package moe.aoramd.raindrop.adapter.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.adapter.list.PlaylistAdapter
import moe.aoramd.raindrop.repository.entity.Song

object BindingPlaylistAdapter {

    @JvmStatic
    @BindingAdapter("playlistAdapter")
    fun setPlaylistAdapter(
        recyclerView: RecyclerView,
        adapter: PlaylistAdapter
    ) {
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("songs")
    fun setSongs(recyclerView: RecyclerView, songs: List<Song>) {
        val adapter = recyclerView.adapter as PlaylistAdapter
        adapter.data = songs
        adapter.notifyDataSetChanged()
    }
}