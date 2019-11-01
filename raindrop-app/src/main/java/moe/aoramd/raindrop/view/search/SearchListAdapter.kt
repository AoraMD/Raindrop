package moe.aoramd.raindrop.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.LayoutSearchItemBinding
import moe.aoramd.raindrop.repository.entity.Song

/**
 *  search interface list adapter
 *
 *  @property fragment search list fragment
 *
 *  @author M.D.
 *  @version dev 1
 */
class SearchListAdapter(private val fragment: SearchListFragment) :
    PagedListAdapter<Song, SearchListAdapter.Companion.SearchListViewHolder>(
        diffCallback
    ) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val binding = DataBindingUtil.inflate<LayoutSearchItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_search_item,
            parent,
            false
        )
        return SearchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { s ->
            holder.binding.apply {
                song = s
                setRootClickListener {
                    fragment.rootClickListener.invoke(s)
                }
                setOperationClickListener { view ->
                    fragment.operationClickListener.invoke(view, s)
                }
            }
        }
    }
}