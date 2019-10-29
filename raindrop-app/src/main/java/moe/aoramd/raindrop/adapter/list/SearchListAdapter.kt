package moe.aoramd.raindrop.adapter.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.LayoutSearchItemBinding
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.search.SearchListFragment

class SearchListAdapter(private val fragment: SearchListFragment) :
    PagedListAdapter<Song, SearchListAdapter.Companion.SearchListViewHolder>(diffCallback) {

    companion object {
        class SearchListViewHolder(val binding: LayoutSearchItemBinding) :
            RecyclerView.ViewHolder(binding.root)

        private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder =
        SearchListViewHolder(
            DataBindingUtil.inflate<LayoutSearchItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.layout_search_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.binding.song = getItem(position)
        holder.binding.rootClickListener = fragment.rootClickListener
        holder.binding.operationClickListener = fragment.operationClickListener
    }
}