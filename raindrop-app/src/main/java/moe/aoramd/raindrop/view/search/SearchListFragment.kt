package moe.aoramd.raindrop.view.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.PopupMenuCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.binding.BindingSearchAdapter
import moe.aoramd.raindrop.adapter.list.SearchListAdapter
import moe.aoramd.raindrop.databinding.FragmentSearchListBinding
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.play.PlayActivity

class SearchListFragment(keywords: String) : Fragment() {

    private lateinit var binding: FragmentSearchListBinding

    private val viewModel: SearchListViewModel by viewModels {
        SearchListViewModel.Factory(keywords)
    }

    private val activityViewModel: SearchHostViewModel by activityViewModels()

    private val adapter = SearchListAdapter(this)

    // Override Functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_search_list,
            container,
            false
        )

        // Search List
        binding.adapter = adapter
        binding.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        // Data Observer
        viewModel.songs.observe(this, Observer { adapter.submitList(it) })

        return binding.root
    }

    // Click Listener
    val rootClickListener = object : BindingSearchAdapter.SongClickListener {
        override fun onClick(view: View, song: Song) {
            // todo not implement
        }
    }

    val operationClickListener = object : BindingSearchAdapter.SongClickListener {
        override fun onClick(view: View, song: Song) {
            PopupMenu(activity!!, view).apply {
                inflate(R.menu.search_item_operation)
                setOnMenuItemClickListener {
                    var result = true
                    when (it.itemId) {
                        R.id.search_item_popup_play_as_next -> {
                            if (activityViewModel.playAsNext(song))
                                PlayActivity.start(activity!!)
                        }
                        R.id.search_item_popup_add_to_playlist -> {
                            // todo not implement
                        }
                        R.id.search_item_popup_download -> {
                            activityViewModel.download(song)
                        }
                        R.id.search_item_popup_share -> {
                            // todo not implement
                        }
                        else -> result = false
                    }
                    result
                }
                show()
            }
        }
    }
}
