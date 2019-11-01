package moe.aoramd.raindrop.view.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.LayoutSongItemBinding
import moe.aoramd.raindrop.repository.entity.Song

/**
 *  recent interface list adapter
 *
 *  @property activity playlist activity
 *
 *  @author M.D.
 *  @version dev 1
 */
class RecentAdapter(private val activity: RecentActivity) :
    PagedListAdapter<Song, RecentAdapter.Companion.RecentViewHolder>(
        diffCallback
    ) {

    companion object {
        class RecentViewHolder(val binding: LayoutSongItemBinding) :
            RecyclerView.ViewHolder(binding.root)

        private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = DataBindingUtil.inflate<LayoutSongItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_song_item,
            parent,
            false
        )
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { s ->
            holder.binding.apply {
                song = s
                setRootClickListener {
                    activity.rootClickListener.invoke(s)
                }
                setOperationClickListener { view ->
                    activity.operationClickListener.invoke(view, s)
                }
            }
        } ?: run {
            holder.binding.song = Song.loading
        }
    }
}