package moe.aoramd.raindrop.view.search

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.ActivitySearchHostBinding
import moe.aoramd.raindrop.view.base.control.BarControlActivity
import moe.aoramd.raindrop.view.base.control.BarControlViewModel

class SearchHostActivity : BarControlActivity() {

    private lateinit var binding: ActivitySearchHostBinding

    private lateinit var searchView: SearchView

    private val viewModel: SearchHostViewModel by viewModels()

    override val barController: BarControlViewModel by lazy { viewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_host)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.search.apply {
            setOnQueryTextListener(queryListener)
            onActionViewExpanded()
        }

        // Attach Music Bar
        attachMusicBar()
    }

    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            viewModel.submit()
            query?.apply { refleshSearchList(this) }
            binding.search.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean = false
    }

    private fun refleshSearchList(keywords: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.search_content, SearchListFragment(keywords))
            .commit()
    }
}
