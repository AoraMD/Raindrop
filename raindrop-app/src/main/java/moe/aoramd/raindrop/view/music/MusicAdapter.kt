package moe.aoramd.raindrop.view.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.LayoutMusicHeaderItemBinding
import moe.aoramd.raindrop.databinding.LayoutMusicPlaylistItemBinding
import moe.aoramd.raindrop.repository.entity.Playlist

/**
 *  music interface list adapter
 *
 *  @property fragment music fragment
 *
 *  @author M.D.
 *  @version dev 1
 */
class MusicAdapter(val fragment: MusicFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        // view type tags

        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_PLAYLIST = 1

        // view holders

        private class HeaderViewHolder(val binding: LayoutMusicHeaderItemBinding) :
            RecyclerView.ViewHolder(binding.root)

        private class PlaylistViewHolder(val binding: LayoutMusicPlaylistItemBinding) :
            RecyclerView.ViewHolder(binding.root)

        // header data class

        data class MusicHeader(
            val id: Long,
            val title: Int,
            val icon: Int
        )
    }

    var headers = listOf<MusicHeader>()
    var playlists = listOf<Playlist>()

    override fun getItemViewType(position: Int): Int =
        if (position < headers.size)
            VIEW_TYPE_HEADER
        else
            VIEW_TYPE_PLAYLIST

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = DataBindingUtil.inflate<LayoutMusicHeaderItemBinding>(
                    layoutInflater,
                    R.layout.layout_music_header_item,
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<LayoutMusicPlaylistItemBinding>(
                    layoutInflater,
                    R.layout.layout_music_playlist_item,
                    parent,
                    false
                )
                PlaylistViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = headers.size + playlists.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        // headers
        if (position < headers.size) {
            val data = headers[position]
            (holder as HeaderViewHolder).binding.apply {
                header = data
                setRootClickListener {
                    fragment.headerItemClickListener.invoke(data.id)
                }
            }
        }

        // playlists
        else {
            val data = playlists[position - headers.size]
            (holder as PlaylistViewHolder).binding.apply {
                playlist = data
                setRootClickListener {
                    fragment.playlistItemClickListener.invoke(data)
                }
            }
        }
    }
}