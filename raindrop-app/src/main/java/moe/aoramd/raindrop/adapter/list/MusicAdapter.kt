package moe.aoramd.raindrop.adapter.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.LayoutMusicHeaderItemBinding
import moe.aoramd.raindrop.databinding.LayoutMusicPlaylistItemBinding
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.view.playlist.PlaylistActivity

class MusicAdapter(val activity: FragmentActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_PLAYLIST = 1

        private class HeaderViewHolder(val binding: LayoutMusicHeaderItemBinding) :
            RecyclerView.ViewHolder(binding.root)

        private class PlaylistViewHolder(val binding: LayoutMusicPlaylistItemBinding) :
            RecyclerView.ViewHolder(binding.root)

        data class MusicHeader(
            val title: Int,
            val icon: Int,
            val click: () -> Unit,
            var trackCount: Int = 0
        )
    }

    private val headers = listOf(
        MusicHeader(
            R.string.music_local_music,
            R.drawable.ic_local_music,
            {}),
        MusicHeader(R.string.music_radio, R.drawable.ic_radio, {}),
        MusicHeader(R.string.music_download, R.drawable.ic_download, {})
    )

    var data = listOf<Playlist>()

    override fun getItemViewType(position: Int): Int {
        return if (position < headers.size) VIEW_TYPE_HEADER else VIEW_TYPE_PLAYLIST
    }

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
                PlaylistViewHolder(
                    binding
                )
            }
        }
    }

    override fun getItemCount(): Int = headers.size + data.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < headers.size) {
            val headerHolder = holder as HeaderViewHolder
            headerHolder.binding.header = headers[position]
        } else {
            val actualPos = position - headers.size
            val playlistHolder = holder as PlaylistViewHolder
            playlistHolder.binding.playlist = data[actualPos]
            playlistHolder.binding.layout.setOnClickListener {
                PlaylistActivity.start(activity, data[actualPos])
            }
        }
    }
}