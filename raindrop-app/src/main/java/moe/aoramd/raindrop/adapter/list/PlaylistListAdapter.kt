package moe.aoramd.raindrop.adapter.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.LayoutSongItemBinding
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.playlist.PlaylistActivity

class PlaylistListAdapter(val activity: PlaylistActivity) :
    RecyclerView.Adapter<PlaylistListAdapter.Companion.SongViewHolder>() {

    companion object {
        class SongViewHolder(val binding: LayoutSongItemBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    var data = listOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = DataBindingUtil.inflate<LayoutSongItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_song_item,
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.binding.index = position
        holder.binding.song = data[position]
        holder.binding.rootClickListener = activity.rootClickListener
        holder.binding.operationClickListener = activity.operationClickListener
    }
}