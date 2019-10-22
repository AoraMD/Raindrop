package moe.aoramd.raindrop.adapter.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.adapter.list.PlaylistListAdapter
import moe.aoramd.raindrop.repository.entity.Song

object BindingPlaylistAdapter {

    @JvmStatic
    @BindingAdapter("playlistAdapter")
    fun setPlaylistListAdapter(
        recyclerView: RecyclerView,
        adapter: PlaylistListAdapter
    ) {
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("songs")
    fun setSongs(recyclerView: RecyclerView, songs: List<Song>) {
        val adapter = recyclerView.adapter as PlaylistListAdapter
        adapter.data = songs
        adapter.notifyDataSetChanged()
    }
}