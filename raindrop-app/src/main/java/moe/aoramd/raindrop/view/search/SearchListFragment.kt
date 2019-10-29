package moe.aoramd.raindrop.view.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.binding.BindingSearchAdapter
import moe.aoramd.raindrop.adapter.list.SearchListAdapter
import moe.aoramd.raindrop.databinding.FragmentSearchListBinding
import moe.aoramd.raindrop.repository.entity.Song

class SearchListFragment(keywords: String) : Fragment() {

    private lateinit var binding: FragmentSearchListBinding

    private val viewModel: SearchListViewModel by viewModels {
        SearchListViewModel.Factory(keywords)
    }

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
            // todo not implement
        }
    }
}
