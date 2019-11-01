package moe.aoramd.raindrop.view.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.LayoutSongItemBinding
import moe.aoramd.raindrop.repository.entity.Song

/**
 *  music play interface playing list adapter
 *
 *  @property activity play activity
 *
 *  @author M.D.
 *  @version dev 1
 */
class PlayPlayingAdapter(val activity: PlayActivity) :
    RecyclerView.Adapter<PlayPlayingAdapter.Companion.SongViewHolder>() {

    companion object {
        class SongViewHolder(val binding: LayoutSongItemBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    var currentIndex = -1

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
        holder.binding.apply {
            elevation = if (position == currentIndex) 8f else 4f
            song = data[position]
            setRootClickListener {
                activity.rootClickListener.invoke(position)
            }
            setOperationClickListener { view ->
                activity.operationClickListener.invoke(view, position)
            }
        }
    }
}