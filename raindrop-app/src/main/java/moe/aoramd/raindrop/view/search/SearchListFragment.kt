package moe.aoramd.raindrop.view.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.FragmentSearchListBinding
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.play.PlayActivity

/**
 *  search interface fragment
 *
 *  @author M.D.
 *  @version dev 1
 */
class SearchListFragment(keywords: String) : Fragment() {

    private lateinit var binding: FragmentSearchListBinding

    private val viewModel: SearchListViewModel by viewModels {
        SearchListViewModel.Factory(keywords)
    }

    private val activityViewModel: SearchHostViewModel by activityViewModels()

    private val adapter = SearchListAdapter(this)

    // override functions

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

        // initialize data binding
        binding.apply {

            // search list
            adapter = this@SearchListFragment.adapter
            layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }

        // paged list data
        viewModel.songs.observe(this, Observer { adapter.submitList(it) })

        return binding.root
    }

    // item click listeners

    internal val rootClickListener: (song: Song) -> Unit = { song ->
        // todo not implement
    }

    internal val operationClickListener: (view: View, song: Song) -> Unit = { view, song ->
        PopupMenu(activity!!, view).apply {
            inflate(R.menu.search_item_operation)
            setOnMenuItemClickListener {
                var result = true
                when (it.itemId) {
                    R.id.search_item_popup_play_as_next -> {
                        if (activityViewModel.playAsNext(song))
                            PlayActivity.start(activity!!)
                    }
                    R.id.playlist_item_popup_add_to_playlist -> {
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
