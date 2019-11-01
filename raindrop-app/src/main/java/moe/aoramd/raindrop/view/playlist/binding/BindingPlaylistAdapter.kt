package moe.aoramd.raindrop.view.playlist.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.view.playlist.PlaylistAdapter
import moe.aoramd.raindrop.repository.entity.Song

/**
 *  playlist interface data binding adapter
 *
 *  @author M.D.
 *  @version dev 1
 */
object BindingPlaylistAdapter {

    /*
       data list recycler view
    */

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
    fun setSongs(
        recyclerView: RecyclerView,
        songs: List<Song>
    ) {
        val adapter = recyclerView.adapter as PlaylistAdapter
        adapter.data = songs
        adapter.notifyDataSetChanged()
    }
}