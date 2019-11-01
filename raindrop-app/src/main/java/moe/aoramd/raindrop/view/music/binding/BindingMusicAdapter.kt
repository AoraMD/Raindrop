package moe.aoramd.raindrop.view.music.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.view.music.MusicAdapter
import moe.aoramd.raindrop.repository.entity.Playlist

/**
 *  music interface data binding adapter
 *
 *  @author M.D.
 *  @version dev 1
 */
object BindingMusicAdapter {

    /*
        data list recycler view
     */

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
    fun setPlaylists(
        recyclerView: RecyclerView,
        playlists: List<Playlist>
    ) {
        val adapter = recyclerView.adapter as MusicAdapter
        adapter.playlists = playlists
        adapter.notifyDataSetChanged()
    }
}